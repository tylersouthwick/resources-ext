package net.northfuse.resources;

import org.springframework.http.MediaType;

/**
 * @author tylers2
 */
public class StyleResourceHandler extends ResourceHandler {
	public StyleResourceHandler() {
		super(new MediaType("text", "css"));
	}
}
