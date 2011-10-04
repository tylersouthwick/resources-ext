package net.northfuse.resources;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.Arrays.asList;

/**
 * @author tylers2
 */
public final class ResourceGenerator implements ApplicationContextAware {
	private final List<String> resourcePaths = new LinkedList<String>();
	private ResourcePatternResolver resourceResolver;

	private static final Logger LOG = LoggerFactory.getLogger(ResourceGenerator.class);

	/**
	 * Creates a resource generator.
	 */
	public ResourceGenerator() {
	}

	/**
	 * Sets the resources.
	 *
	 * @param resources The new resources to add
	 */
	public void setResources(List<String> resources) {
		this.resourcePaths.addAll(resources);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.resourceResolver = applicationContext;
	}

	/**
	 * Resolves resources.
	 *
	 * @return A list of resolved resources
	 */
	private List<Resource> resolveResources() {
		List<Resource> resolvedResources = new LinkedList<Resource>();
		boolean debug = LOG.isDebugEnabled();
		if (debug) {
			LOG.debug("resolving resources");
		}
		for (String resourcePath : resourcePaths) {
			if (debug) {
				LOG.debug("resolving resource path [" + resourcePath + "]");
			}
			try {
				List<Resource> resources = newLinkedList(
						filter(asList(resourceResolver.getResources(resourcePath)),
								new Predicate<Resource>() {
									@Override
									public boolean apply(Resource input) {
										return !input.getDescription().contains("webinf");
									}
								}));

				if (debug) {
					LOG.debug("Found " + resources.size() + " resources:");
					for (Resource resource : resources) {
						LOG.debug("\t" + resource.getDescription());
					}
				}
				Collections.sort(resources, new Comparator<Resource>() {
					@Override
					public int compare(Resource o1, Resource o2) {
						return o1.getDescription().compareTo(o2.getDescription());
					}
				});
				resolvedResources.addAll(resources);
			} catch (IOException e) {
				throw new IllegalStateException("Unable to get resources for resourcePath [" + resourcePath + "]", e);
			}
		}
		return resolvedResources;
	}

	/**
	 * Builds the resource from all the given resources.
	 *
	 * @return The constructed resource
	 */
	public AggregatedResource getAggregatedResource() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (Resource resource : resolveResources()) {
			try {
				LOG.debug("Adding " + resource.getDescription());
				InputStream is = new LineWrapperInputStream(resource.getInputStream(), resource.getDescription());
				FileCopyUtils.copy(is, baos);
				baos.write('\n');
			} catch (IOException e) {
				throw new IllegalStateException("Unable to copy resource file [" + resource.getDescription() + "]", e);
			}
		}

		final byte[] data = baos.toByteArray();
		LOG.debug("Built resource with " + data.length + " bytes @" + new Date());

		return new AggregatedResource(data);
	}
}
