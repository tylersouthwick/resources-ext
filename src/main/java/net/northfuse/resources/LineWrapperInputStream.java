package net.northfuse.resources;

import java.io.*;

/**
 * Appends a file and line number to the beginning of all lines.
 *
 * @author tylers2
 */
public final class LineWrapperInputStream extends InputStream {
	private final InputStream is;

	/**
	 * Creates a new LineWrapperInputStream.
	 *
	 * @param in The input stream to wrap
	 * @param description The description of the input stream
	 *
	 * @throws IOException if the underlying InputStream has an IOException
	 */
	public LineWrapperInputStream(InputStream in, String description) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		int lineNumber = 0;
		boolean inComment = false;
		while ((line = reader.readLine()) != null) {
			if (inComment) {
				writer.println("   " + description + ":" + (++lineNumber) + "   " + line);
			} else {
				writer.println("/* " + description + ":" + (++lineNumber) + " */" + line);
			}
			if (!inComment) {
				int commentIndex = line.lastIndexOf("/*");
				if (commentIndex > -1) {
					if (commentIndex > 0) {
						//is the comment start inside of a string?
						if (inString(line.substring(0, commentIndex))) {
							continue;
						}
					}
					/**
					 * We are not in a comment if:
					 * 1) there is an ending multi-comment block after, or
					 * 2) a single comment before
					 */
					if (!(line.substring(commentIndex).contains("*/") || line.substring(0, commentIndex).contains("//"))) {
						inComment = true;
					}
				}
			} else {
				int commentIndex = line.lastIndexOf("*/");
				if (commentIndex > -1) {
					if (!line.substring(commentIndex).contains("/*")) {
						inComment = false;
					}
				}
			}
		}
		writer.close();
		//get rid of the last new line
		byte[] data = baos.toByteArray();
		this.is = new ByteArrayInputStream(data, 0, data.length - 1);
	}

	/**
	 * Checks to see if the line ends in a string.
	 * @param line The line to check
	 *
	 * @return Whether or not in string
	 */
	private boolean inString(String line) {
		//there should be an odd number of double quotes to be in a string
		return countOccurences(line, '"') % 2 != 0;
	}

	/**
	 * Count the occurences of a pattern in a string.
	 *
	 * @param s The string to check
	 * @param pattern The pattern
	 *
	 * @return The count of occurrences
	 */
	private int countOccurences(String s, char pattern) {
		int count = 0;
		for (char c : s.toCharArray()) {
			if (c == pattern) {
				count++;
			}
		}
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read() throws IOException {
		return is.read();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long skip(long n) throws IOException {
		return is.skip(n);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int available() throws IOException {
		return is.available();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		is.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mark(int readlimit) {
		is.mark(readlimit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() throws IOException {
		is.reset();
	}

	/**
	 * @{inheritDoc}
	 *
	 * @return @{inheritDoc}
	 */
	@Override
	public boolean markSupported() {
		return is.markSupported();
	}
}
