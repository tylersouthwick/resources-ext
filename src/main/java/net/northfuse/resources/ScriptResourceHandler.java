package net.northfuse.resources;

import org.springframework.http.MediaType;

/**
 * @author tylers2
 */
public class ScriptResourceHandler extends ResourceHandler {
	public ScriptResourceHandler() {
		super(new MediaType("text", "javascript"));
	}
}
