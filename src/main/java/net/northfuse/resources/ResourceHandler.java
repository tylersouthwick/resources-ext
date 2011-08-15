package net.northfuse.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import sun.security.util.Resources_es;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tylers2
 */
public abstract class ResourceHandler implements ApplicationContextAware {
	private final List<String> resourcePaths = new LinkedList<String>();
	private final List<Resource> resources = new LinkedList<Resource>();
	private final MediaType mediaType;
	private boolean debug;
	private Resource resource;
	private String name;
	private ApplicationContext applicationContext;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	ResourceHandler(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResources(List<String> resources) {
		this.resourcePaths.addAll(resources);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Resource aggregatedResource() {
		if (debug) {
			synchronized (this.resources) {
				this.resources.clear();
				resolveResources();
				return buildResource();
			}
		} else {
			return resource;
		}
	}

	@PostConstruct
	public void init() {
		resolveResources();
		generateResource();
	}

	private void resolveResources() {
		for (String resourcePath : resourcePaths) {
			try {
				Resource[] resources = applicationContext.getResources(resourcePath);
				this.resources.addAll(Arrays.asList(resources));
			} catch (IOException e) {
				throw new IllegalStateException("Unable to get resources for resourcePath [" + resourcePath + "]", e);
			}
		}
	}

	private void generateResource() {
		resource = buildResource();
	}

	private Resource buildResource() {
		final byte[] data = aggregate();
		final long lastModified = new Date().getTime();
		return new ByteArrayResource(data) {
			@Override
			public long lastModified() throws IOException {
				return lastModified;
			}

			@Override
			public long contentLength() throws IOException {
				return data.length;
			}
		};
	}

	public byte[] aggregate() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LOG.debug("Building " + name);
		for (Resource resource : resources) {
			try {
				LOG.debug("Adding " + resource.getDescription());
				FileCopyUtils.copy(resource.getInputStream(), baos);
			} catch (IOException e) {
				throw new IllegalStateException("Unable to copy resource file [" + resource.getDescription() + "]", e);
			}
		}
		LOG.debug("Built " + name);
		return baos.toByteArray();
	}

	public MediaType getMediaType() {
		return mediaType;
	}
}
