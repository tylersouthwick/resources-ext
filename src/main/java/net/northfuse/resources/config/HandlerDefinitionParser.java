package net.northfuse.resources.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author tylers2
 */
public class HandlerDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String sOrder = element.getAttribute("order");
		Integer order = null;
		if (StringUtils.hasText(sOrder)) {
			order = Integer.parseInt(sOrder);
		}
		ResourceDefinitionParser.registerHandlers(parserContext, order);
		return null;
	}
}
