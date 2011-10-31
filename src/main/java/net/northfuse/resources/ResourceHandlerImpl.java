package net.northfuse.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * @author tylers2
 */
public abstract class ResourceHandlerImpl implements ResourceHandler {
	private String mapping;
	private Resource resource;
	private ResourceGenerator resourceGenerator;
	private boolean debug;
	private final MediaType mediaType;

	private static final Logger LOG = LoggerFactory.getLogger(ResourceHandler.class);

	/**
	 * Creates a resource handler.
	 *
	 * @param mediaType The MediaType
	 */
	ResourceHandlerImpl(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * Sets debug mode.
	 *
	 * @param debug Whether or not to enable debug mode
	 */
	public final void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDebug() {
		return debug;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getMapping() {
		return mapping;
	}

	/**
	 * Sets the mapping.
	 *
	 * @param mapping The mapping
	 */
	public final void setMapping(String mapping) {
		this.mapping = mapping;
	}

	/**
	 * Sets the resource generator.
	 * @param resourceGenerator The resource generator
	 */
	public final void setResourceGenerator(ResourceGenerator resourceGenerator) {
		this.resourceGenerator = resourceGenerator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Resource getAggregatedResource() {
		if (debug) {
			LOG.debug("Loading resource: " + getMapping());
			Resource resource = resourceGenerator.getAggregatedResource(true);
			LOG.debug("Loaded resource: " + getMapping());
			return resource;
		} else {
			return resource;
		}
	}

	/**
	 * Initialize (cache) resource.
	 *
	 * @throws IOException IOException
	 */
	@PostConstruct
	public final void init() throws IOException {
		if (!debug) {
			LOG.info("Initializing resource: " + getMapping());
			AggregatedResource resource = resourceGenerator.getAggregatedResource(false);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = wrapWithMinify(resource.getInputStream());
			FileCopyUtils.copy(is, baos);
			this.resource = new AggregatedResource(baos.toByteArray());
			LOG.info("Initialized Resource: " + getMapping());
		}
	}

	/**
	 * Wraps the input stream with an input stream that minifies it.
	 *
	 * @param is The input stream to wrap
	 * @return A wrapped input stream
	 * @throws IOException If an io exception occurs.
	 */
	protected abstract InputStream wrapWithMinify(InputStream is) throws IOException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MediaType getMediaType() {
		return mediaType;
	}
}
