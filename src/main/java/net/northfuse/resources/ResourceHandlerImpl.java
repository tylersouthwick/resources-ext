package net.northfuse.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * @author tylers2
 */
public abstract class ResourceHandlerImpl implements ResourceHandler, ApplicationContextAware {
	private final List<String> resourcePaths = new LinkedList<String>();
	private final List<Resource> resources = new LinkedList<Resource>();
	private final MediaType mediaType;
	private boolean debug;
	private Resource resource;
	private String mapping;
	private ResourcePatternResolver resourceResolver;

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
	 * Sets the resources.
	 *
	 * @param resources The new resources to add
	 */
	public final void setResources(List<String> resources) {
		this.resourcePaths.addAll(resources);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setApplicationContext(ApplicationContext applicationContext) {
		setResourceResolver(applicationContext);
	}

	/**
	 * Sets the resource resolver.
	 * @param resourceResolver The resource resolver
	 */
	public final void setResourceResolver(ResourcePatternResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Resource getAggregatedResource() {
		if (debug) {
			synchronized (this.resources) {
				this.resources.clear();
				resolveResources();
				return buildResource(false);
			}
		} else {
			return resource;
		}
	}

	/**
	 * Initialize (cache) resource.
	 */
	@PostConstruct
	public final void init() {
		try {
			if (!debug) {
				resolveResources();
				generateResource();
			}
		} catch (Throwable t) {
			LOG.error("Unable to initialize resource: " + mapping, t);
		}
	}

	/**
	 * resolves resources.
	 */
	private void resolveResources() {
		boolean debug = LOG.isDebugEnabled();
		if (debug) {
			LOG.debug("resolving resources");
		}
		for (String resourcePath : resourcePaths) {
			if (debug) {
				LOG.debug("resolving resource path [" + resourcePath + "]");
			}
			try {
				Resource[] resources = resourceResolver.getResources(resourcePath);
				if (debug) {
					LOG.debug("Found " + resources.length + " resources:");
					for (Resource resource : resources) {
						LOG.debug("\t" + resource.getDescription());
					}
				}
				List<Resource> resourceList = new ArrayList<Resource>(Arrays.asList(resources));
				Collections.sort(resourceList, new Comparator<Resource>() {
					@Override
					public int compare(Resource o1, Resource o2) {
						return o1.getDescription().compareTo(o2.getDescription());
					}
				});
				this.resources.addAll(resourceList);
			} catch (IOException e) {
				throw new IllegalStateException("Unable to get resources for resourcePath [" + resourcePath + "]", e);
			}
		}
	}

	/**
	 * Generates resource and caches it.
	 */
	private void generateResource() {
		resource = buildResource(true);
	}

	/**
	 * Builds the resource from all the given resources.
	 *
	 * @param minify Whether or not to minify
	 *
	 * @return The constructed resource
	 */
	private Resource buildResource(boolean minify) {
		final byte[] data = aggregate(minify);
		LOG.debug("Built resource with " + data.length + " bytes @" + new Date());
		resource = new ByteArrayResource(data) {
			@Override
			public long lastModified() throws IOException {
				return -1;
			}

			@Override
			public long contentLength() throws IOException {
				return data.length;
			}
		};
		return resource;
	}

	/**
	 * Aggregates the resources.
	 *
	 * @param minify Whether or not to minify.
	 *
	 * @return A non-null byte array
	 */
	private byte[] aggregate(boolean minify) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LOG.debug("Building " + mapping);
		for (Resource resource : resources) {
			try {
				LOG.debug("Adding " + resource.getDescription());
				InputStream is = resource.getInputStream();
				if (minify) {
					is = wrapWithMinify(is);
				} else if (debug) {
					String description = resource.getDescription();
					String text = "/WEB-INF/classes";
					int index = description.indexOf(text);
					if (index > 0) {
						description = description.substring(index + text.length());
					}
					is = new LineWrapperInputStream(is, description);
				}
				FileCopyUtils.copy(is, baos);
				baos.write('\n');
			} catch (IOException e) {
				throw new IllegalStateException("Unable to copy resource file [" + resource.getDescription() + "]", e);
			}
		}
		LOG.debug("Built " + mapping);
		return baos.toByteArray();
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
