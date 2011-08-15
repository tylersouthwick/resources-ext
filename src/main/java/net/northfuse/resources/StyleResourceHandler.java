package net.northfuse.resources;

import com.yahoo.platform.yui.compressor.CssCompressor;
import org.springframework.http.MediaType;

import java.io.*;

/**
 * @author tylers2
 */
public class StyleResourceHandler extends ResourceHandler {
	public StyleResourceHandler() {
		super(new MediaType("text", "css"));
	}

	@Override
	protected InputStream wrapWithMinify(InputStream is) throws IOException {
		CssCompressor compressor = new CssCompressor(new InputStreamReader(is));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		compressor.compress(writer, 0);
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
