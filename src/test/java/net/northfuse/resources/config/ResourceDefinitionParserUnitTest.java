package net.northfuse.resources.config;

import junit.framework.Assert;
import net.northfuse.resources.ResourceHandler;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author tylers2
 */
public class ResourceDefinitionParserUnitTest {

	@Test
	public void checkNoDebugSet() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
		context.setConfigLocation("classpath:/net/northfuse/resources/config/checkNoDebugSet-context.xml");
		context.refresh();

		ResourceHandler resourceHandler = context.getBean(ResourceHandler.class);
		Assert.assertNotNull(resourceHandler);
		Assert.assertFalse("By default, a resource is not in debug mode", resourceHandler.isDebug());
	}
}
