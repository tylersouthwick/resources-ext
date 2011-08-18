package net.northfuse.resources.config;

import net.northfuse.resources.ScriptResourceHandler;

/**
 * @author tylers2
 */
public final class ScriptDefinitionParser extends ResourceDefinitionParser<ScriptResourceHandler> {

	@Override
	protected Class<ScriptResourceHandler> getImplementation() {
		return ScriptResourceHandler.class;
	}
}
