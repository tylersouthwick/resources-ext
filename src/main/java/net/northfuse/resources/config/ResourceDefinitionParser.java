package net.northfuse.resources.config;

import net.northfuse.resources.ResourceHandler;
import net.northfuse.resources.ResourceHandlerAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author tylers2
 */
abstract class ResourceDefinitionParser<T extends ResourceHandler> implements BeanDefinitionParser {
	private static final AtomicBoolean registeredAdapter = new AtomicBoolean();

	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition definition = new RootBeanDefinition(getImplementation());

		definition.getPropertyValues().add("debug", element.getAttribute("debug"));
		String mapping = element.getAttribute("mapping");
		definition.getPropertyValues().add("mapping", mapping);

		//find resource location
		List<String> resources = new LinkedList<String>();

		final NodeList nodeList = element.getElementsByTagNameNS("http://northfuse.net/schema/resources-ext", "resource");
		final Iterator<Node> i = new Iterator<Node>() {
			private int count = 0;
			public boolean hasNext() {
				return count < nodeList.getLength();
			}

			public Node next() {
				return nodeList.item(count++);
			}

			public void remove() {
			}
		};
		for (Node node : new Iterable<Node>() {
			public Iterator<Node> iterator() {
				return i;
			}
		}) {
			Element e = (Element) node;
			resources.add(e.getAttribute("location"));
		}

		definition.getPropertyValues().add("resources", resources);

		parserContext.getRegistry().registerBeanDefinition(getImplementation().getName() + "::" + mapping, definition);

		registerAdapter(parserContext);

		String sOrder = element.getAttribute("order");
		if (StringUtils.hasText(sOrder)) {
			int order = Integer.parseInt(sOrder);
			definition.getPropertyValues().add("order", order);
		}

		return definition;
	}

	static void registerAdapter(ParserContext parserContext) {
		synchronized (registeredAdapter) {
			if (!registeredAdapter.get()) {
				registeredAdapter.set(true);

				parserContext.getRegistry().registerBeanDefinition(ResourceHandlerAdapter.class.getName(), new RootBeanDefinition(ResourceHandlerAdapter.class));
			}
		}

	}

	protected abstract Class<T> getImplementation();
}
