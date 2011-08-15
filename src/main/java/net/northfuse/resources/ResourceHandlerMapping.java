package net.northfuse.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.List;

/**
 * @author tylers2
 */
public class ResourceHandlerMapping extends SimpleUrlHandlerMapping {

	@Autowired
	public ResourceHandlerMapping(List<ResourceHandler> handlers) {
		for (ResourceHandler handler : handlers) {
			registerHandler(handler.getName(), handler);
		}
	}
}
