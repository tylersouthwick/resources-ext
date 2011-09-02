package net.northfuse.resources.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author tylers2
 */
public final class ResourceNamespaceHandler extends NamespaceHandlerSupport {

	public static final String NAMESPACE = "http://northfuse.net/schema/resources-ext";

	/**
	 * Registers the bean definition parsers for the script and style config elements.
	 */
	public void init() {
		ScriptDefinitionParser scriptDefinitionParser = new ScriptDefinitionParser();
		StyleDefinitionParser styleDefinitionParser = new StyleDefinitionParser();

		registerBeanDefinitionParser("script", scriptDefinitionParser);
		registerBeanDefinitionParser("style", styleDefinitionParser);

		registerBeanDefinitionParser("resources", new ResourcesDefinitionParser(scriptDefinitionParser, styleDefinitionParser));
	}
}
