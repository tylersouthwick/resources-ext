package net.northfuse.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tylers2
 */
abstract class ResourceHandler {
	private final List<Resource> resources = new LinkedList<Resource>();
	private final MediaType mediaType;
	private boolean debug;
	private Resource resource;
	private String name;

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

	public void setResources(List<Resource> resources) {
		this.resources.addAll(resources);
	}

	public Resource aggregatedResource() {
		if (debug) {
			return buildResource();
		} else {
			return resource;
		}
	}

	@PostConstruct
	public void generateResource() {
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
