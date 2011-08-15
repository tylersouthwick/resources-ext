package net.northfuse.resources.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author tylers2
 */
public class ResourceNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("script", new ScriptDefinitionParser());
	}
}
