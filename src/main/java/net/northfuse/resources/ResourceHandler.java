package net.northfuse.resources;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

/**
 * @author tylers2
 */
public interface ResourceHandler {

	/**
	 * Gets whether or not this is in debug mode.
	 *
	 * @return debug flag
	 */
	boolean isDebug();

	/**
	 * Gets the mapping of this resource.
	 *
	 * @return The mapping
	 */
	String getMapping();

	/**
	 * Aggregates the resource, creating a new resource.
	 *
	 * @return a non-null resource
	 */
	Resource getAggregatedResource();

	/**
	 * Gets the media type.
	 *
	 * @return A non-null media-type
	 */
	MediaType getMediaType();
}
