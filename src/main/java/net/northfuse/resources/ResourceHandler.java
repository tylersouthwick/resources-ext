package net.northfuse.resources;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tylers2
 */
public class ResourceHandler {
	private final List<Resource> resources = new LinkedList<Resource>();

	public void setResources(List<Resource> resources) {
		this.resources.addAll(resources);
	}

	public Resource aggregatedResource() {
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
		for (Resource resource : resources) {
			try {
				FileCopyUtils.copy(resource.getInputStream(), baos);
			} catch (IOException e) {
				throw new IllegalStateException("Unable to copy resource file [" + resource.getDescription() + "]", e);
			}
		}
		return baos.toByteArray();
	}
}
