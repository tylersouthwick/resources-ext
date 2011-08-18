package net.northfuse.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yahoo.platform.yui.compressor.CssCompressor;
import org.springframework.http.MediaType;

import java.io.*;

/**
 * @author tylers2
 */
public final class StyleResourceHandler extends ResourceHandler {

	private static final Logger LOG = LoggerFactory.getLogger(StyleResourceHandler.class);

	/**
	 * Creates a StyleResourceHandler.
	 */
	public StyleResourceHandler() {
		super(new MediaType("text", "css"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected InputStream wrapWithMinify(InputStream is) throws IOException {
		CssCompressor compressor = new CssCompressor(new InputStreamReader(is));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		compressor.compress(writer, 0);
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
