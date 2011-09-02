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

	/**
	 * Creates a ResourcesDefinitionParser.
	 *
	 * @param scriptDefinitionParser The script definition parser
	 * @param styleDefinitionParser The style definition parser
	 */
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
		String order = element.getAttribute("order");
		String mapping = element.getAttribute("mapping");

		handleElement(element, "script", scriptDefinitionParser, parserContext, debug, mapping, order);
		handleElement(element, "style", styleDefinitionParser, parserContext, debug, mapping, order);

		return null;
	}

	/**
	 * Handles the element.
	 * @param element The element
	 * @param name The element name
	 * @param parser The bean definition parser
	 * @param parserContext The parser context
	 * @param debug default debug value
	 * @param mapping The base mapping
	 * @param order The default order
	 */
	private void handleElement(Element element, String name, ResourceDefinitionParser parser, ParserContext parserContext, boolean debug, String mapping, String order) {
		final NodeList nodeList = element.getElementsByTagNameNS(ResourceNamespaceHandler.NAMESPACE, name);
		for (Element e : new NodeListIterator<Element>(nodeList)) {
			parser.doParse(parserContext, e, debug, mapping, order);
		}
	}

}
