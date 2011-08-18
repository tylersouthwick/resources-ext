package net.northfuse.resources.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author tylers2
 */
public final class ResourceNamespaceHandler extends NamespaceHandlerSupport {

	/**
	 * Registers the bean definition parsers for the script and style config elements.
	 */
	public void init() {
		registerBeanDefinitionParser("script", new ScriptDefinitionParser());
		registerBeanDefinitionParser("style", new StyleDefinitionParser());
	}
}
