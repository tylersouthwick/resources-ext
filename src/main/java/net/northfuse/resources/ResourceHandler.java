package net.northfuse.resources;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private String mapping;
	private ApplicationContext applicationContext;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	ResourceHandler(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
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
				return buildResource(true);
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
		resource = buildResource(true);
	}

	private ByteArrayResource buildResource(boolean minify) {
		final byte[] data = aggregate(minify);
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

	private byte[] aggregate(boolean minify) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LOG.debug("Building " + mapping);
		for (Resource resource : resources) {
			try {
				LOG.debug("Adding " + resource.getDescription());
				InputStream is = resource.getInputStream();
				if (minify) {
					is = wrapWithMinify(is);
				}
				FileCopyUtils.copy(is, baos);
			} catch (IOException e) {
				throw new IllegalStateException("Unable to copy resource file [" + resource.getDescription() + "]", e);
			}
		}
		LOG.debug("Built " + mapping);
		return baos.toByteArray();
	}

	protected abstract InputStream wrapWithMinify(InputStream is) throws IOException;

	public MediaType getMediaType() {
		return mediaType;
	}
}
