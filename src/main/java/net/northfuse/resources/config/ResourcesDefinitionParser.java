package net.northfuse.resources.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author tylers2
 */
public class ResourcesDefinitionParser implements BeanDefinitionParser {

	private final ScriptDefinitionParser scriptDefinitionParser;
	private final StyleDefinitionParser styleDefinitionParser;

	public ResourcesDefinitionParser(ScriptDefinitionParser scriptDefinitionParser, StyleDefinitionParser styleDefinitionParser) {
		this.scriptDefinitionParser = scriptDefinitionParser;
		this.styleDefinitionParser = styleDefinitionParser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		boolean debug = "true".equals(element.getAttribute("debug"));
		Integer order = convertToInt(element.getAttribute("order"));
		String mapping = element.getAttribute("mapping");

		handleElement(element, "script", scriptDefinitionParser, parserContext, debug, mapping);
		handleElement(element, "style", styleDefinitionParser, parserContext, debug, mapping);

		return null;
	}

	private void handleElement(Element element, String name, ResourceDefinitionParser parser, ParserContext parserContext, boolean debug, String mapping) {
		final NodeList nodeList = element.getElementsByTagNameNS(ResourceNamespaceHandler.NAMESPACE, name);
		for (Element e : new NodeListIterator<Element>(nodeList)) {
			parser.doParse(parserContext, e, debug, mapping);
		}
	}

	private Integer convertToInt(String order) {
		if (StringUtils.hasText(order)) {
			return Integer.parseInt(order);
		} else {
			return null;
		}
	}
}
