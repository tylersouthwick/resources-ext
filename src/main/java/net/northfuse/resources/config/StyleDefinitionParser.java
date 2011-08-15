package net.northfuse.resources.config;

import net.northfuse.resources.StyleResourceHandler;

/**
 * @author tylers2
 */
public class StyleDefinitionParser extends ResourceDefinitionParser<StyleResourceHandler> {

	@Override
	protected Class<StyleResourceHandler> getImplementation() {
		return StyleResourceHandler.class;
	}
}
