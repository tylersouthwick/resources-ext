package net.northfuse.resources;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tylers2
 */
public class ResourceHandlerAggregationUnitTest {
	@Test
	public void checkLastModified() throws IOException {
		ResourceHandler resourceHandler = new ResourceHandler(MediaType.APPLICATION_JSON) {
			@Override
			protected InputStream wrapWithMinify(InputStream is) throws IOException {
				return is;
			}
		};
		resourceHandler.setResources(Arrays.asList("test"));
		resourceHandler.setDebug(true);
		final LastModifiedResource resource = new LastModifiedResource();
		resourceHandler.setResourceResolver(new ResourcePatternResolver() {
			@Override
			public Resource[] getResources(String locationPattern) throws IOException {
				return new Resource[] {resource};
			}

			@Override
			public Resource getResource(String location) {
				throw new UnsupportedOperationException();
			}

			@Override
			public ClassLoader getClassLoader() {
				throw new UnsupportedOperationException();
			}
		});

		Assert.assertTrue(checkLastModified(resource, resourceHandler, 5));
		Assert.assertTrue(checkLastModified(resource, resourceHandler, 25));
		Assert.assertFalse(checkLastModified(resource, resourceHandler, 20));
		Assert.assertTrue(checkLastModified(resource, resourceHandler, 25));
	}

	private boolean checkLastModified(LastModifiedResource resource, ResourceHandler resourceHandler, int lastModified) throws IOException {
		resource.lastModified = lastModified;
		Resource aggregatedResource = resourceHandler.aggregatedResource();
		Assert.assertNotNull(aggregatedResource);
		return lastModified == aggregatedResource.lastModified();
	}

	private static class LastModifiedResource implements Resource {
		private long lastModified = 0;

		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public boolean isReadable() {
			return false;
		}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public URL getURL() throws IOException {
			return null;
		}

		@Override
		public URI getURI() throws IOException {
			return null;
		}

		@Override
		public File getFile() throws IOException {
			return null;
		}

		@Override
		public long contentLength() throws IOException {
			return 0;
		}

		@Override
		public long lastModified() throws IOException {
			return lastModified;
		}

		@Override
		public Resource createRelative(String relativePath) throws IOException {
			return null;
		}

		@Override
		public String getFilename() {
			return null;
		}

		@Override
		public String getDescription() {
			return "test";
		}

		@Override
		public InputStream getInputStream() throws IOException {
			byte[] data = new byte[0];
			return new ByteArrayInputStream(data);
		}

		public void increment() {
			lastModified++;
		}
	}
}
