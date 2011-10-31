package net.northfuse.resources;

import com.yahoo.platform.yui.compressor.CssCompressor;
import org.springframework.http.MediaType;

import java.io.*;

/**
 * @author tylers2
 */
public final class StyleResourceHandler extends ResourceHandlerImpl {

	private static final int LINEBREAK = 800;

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
		compressor.compress(writer, LINEBREAK);
		writer.close();
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
