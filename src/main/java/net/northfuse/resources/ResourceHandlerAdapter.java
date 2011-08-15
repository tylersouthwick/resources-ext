package net.northfuse.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tylers2
 */
public class ResourceHandlerAdapter extends WebContentGenerator implements HandlerAdapter {

	private final static Logger LOG = LoggerFactory.getLogger(ResourceHandlerAdapter.class);

	public ResourceHandlerAdapter() {
		super(METHOD_GET);
	}

	@Override
	public boolean supports(Object handler) {
		return ResourceHandler.class.isAssignableFrom(handler.getClass());
	}

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		checkAndPrepare(request, response, true);
		ResourceHandler handler = (ResourceHandler) o;
		Resource resource = handler.aggregatedResource();

		// check the resource's media type
		MediaType mediaType = getMediaType(resource);
		if (mediaType != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Determined media type [" + mediaType + "] for " + resource);
			}
		}
		else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No media type found for " + resource + " - returning 404");
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		// header phase
		setHeaders(response, resource, mediaType);
		if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
			LOG.debug("Resource not modified - returning 304");
			return null;
		}

		writeContent(response, resource);
		return null;
	}

	@Override
	public long getLastModified(HttpServletRequest request, Object handler) {
		return 0;
	}

	/**
	 * Determine an appropriate media type for the given resource.
	 * @param resource the resource to check
	 * @return the corresponding media type, or <code>null</code> if none found
	 */
	protected MediaType getMediaType(Resource resource) {
		/*
		String mimeType = getServletContext().getMimeType(resource.getFilename());
		return (StringUtils.hasText(mimeType) ? MediaType.parseMediaType(mimeType) : null);
		*/
		return MediaType.APPLICATION_JSON;
	}

	/**
	 * Set headers on the given servlet response.
	 * Called for GET requests as well as HEAD requests.
	 * @param response current servlet response
	 * @param resource the identified resource (never <code>null</code>)
	 * @param mediaType the resource's media type (never <code>null</code>)
	 * @throws IOException in case of errors while setting the headers
	 */
	protected void setHeaders(HttpServletResponse response, Resource resource, MediaType mediaType) throws IOException {
		long length = resource.contentLength();
		if (length > Integer.MAX_VALUE) {
			throw new IOException("Resource content too long (beyond Integer.MAX_VALUE): " + resource);
		}
		response.setContentLength((int) length);
		response.setContentType(mediaType.toString());
	}

	/**
	 * Write the actual content out to the given servlet response,
	 * streaming the resource's content.
	 * @param response current servlet response
	 * @param resource the identified resource (never <code>null</code>)
	 * @throws IOException in case of errors while writing the content
	 */
	protected void writeContent(HttpServletResponse response, Resource resource) throws IOException {
		FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
	}
}
