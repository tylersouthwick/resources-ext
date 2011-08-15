package net.northfuse.resources.config;

import net.northfuse.resources.ResourceHandler;
import net.northfuse.resources.ResourceHandlerAdapter;
import net.northfuse.resources.ResourceHandlerMapping;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.web.servlet.HandlerAdapter;
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
	private static final AtomicBoolean registeredHandler = new AtomicBoolean();

	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition definition = new RootBeanDefinition(getImplementation());

		definition.getPropertyValues().add("debug", element.getAttribute("debug"));
		String mapping = element.getAttribute("mapping");
		definition.getPropertyValues().add("name", mapping);

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

		registerHandlers(parserContext);

		return definition;
	}

	static void registerHandlers(ParserContext parserContext) {
		registerHandlers(parserContext, null);
	}

	public static void registerHandlers(ParserContext parserContext, Integer order) {
		String name = ResourceHandlerMapping.class.getName();
		synchronized (registeredHandler) {
			if (!registeredHandler.get()) {
				registeredHandler.set(true);

				parserContext.getRegistry().registerBeanDefinition(ResourceHandlerAdapter.class.getName(), new RootBeanDefinition(ResourceHandlerAdapter.class));
				RootBeanDefinition definition = new RootBeanDefinition(ResourceHandlerMapping.class);
				if (order != null) {
					definition.getPropertyValues().add("order", order);
				}
				parserContext.getRegistry().registerBeanDefinition(name, definition);
			}
		}

	}

	protected abstract Class<T> getImplementation();
}
