package net.northfuse.resources;

/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
*/
import org.springframework.http.MediaType;

import java.io.*;

/**
 * @author tylers2
 */
public final class ScriptResourceHandler extends ResourceHandlerImpl {

	//private static final Logger LOG = LoggerFactory.getLogger(ScriptResourceHandler.class);
//	private static final int LINEBREAK = 800;

	/**
	 * Creates a ScriptResourceHandler.
	 */
	public ScriptResourceHandler() {
		super(new MediaType("text", "javascript"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected InputStream wrapWithMinify(InputStream is) throws IOException {
		/*
		int linebreak = LINEBREAK;
		boolean munge = true;
		boolean verbose = false;
		boolean preserveAllSemiColons = true;
		boolean disableOptimizations = true;

		InputStreamReader reader = new InputStreamReader(is);
		JavaScriptCompressor compressor = new JavaScriptCompressor(reader, errorReporter);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		compressor.compress(writer, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);
		writer.close();
		return new ByteArrayInputStream(baos.toByteArray());
		*/
		return is;
	}

	/*
	private final ErrorReporter errorReporter = new ErrorReporter() {
		@Override
		public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
			LOG.warn(createMessage(message, sourceName, line, lineSource, lineOffset));
		}

		@Override
		public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
			LOG.error(createMessage(message, sourceName, line, lineSource, lineOffset));
		}

		private String createMessage(String message, String sourceName, int line, String lineSource, int lineOffset) {
			return message + " @" + sourceName + ":" + line + ":" + lineOffset + " - " + lineSource;
		}

		@Override
		public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
			return null;
		}
	};
	*/
}
