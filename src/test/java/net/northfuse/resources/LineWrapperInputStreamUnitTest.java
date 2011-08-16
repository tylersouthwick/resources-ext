package net.northfuse.resources;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tylers2
 */
public class LineWrapperInputStreamUnitTest {

	@Test
	public void multiLineComment() throws IOException {
		String description = "HelloWorld";
		List<String> input = new LinkedList<String>();
		input.add("/* a");
		input.add("* b");
		input.add("* c");
		input.add("*/d");
		input.add("e");

		List<String> expected = new LinkedList<String>();
		expected.add("/* " + description + ":1 */" + "/* a");
		expected.add("   " + description + ":2   " + "* b");
		expected.add("   " + description + ":3   " + "* c");
		expected.add("   " + description + ":4   " + "*/d");
		expected.add("/* " + description + ":5 */" + "e");

		assertEqual(input, expected, description);
	}

	@Test
	public void commentCharacterInString() throws IOException {
		String description = "HelloWorld";
		List<String> input = new LinkedList<String>();
		input.add("e \"/*\"");
		input.add("f");

		List<String> expected = new LinkedList<String>();
		expected.add("/* " + description + ":1 */" + "e \"/*\"");
		expected.add("/* " + description + ":2 */" + "f");

		assertEqual(input, expected, description);
	}

	@Test
	public void noCommentCharacterInString() throws IOException {
		String description = "HelloWorld";
		List<String> input = new LinkedList<String>();
		input.add("e \"\"/*");
		input.add("f");

		List<String> expected = new LinkedList<String>();
		expected.add("/* " + description + ":1 */" + "e \"\"/*");
		expected.add("   " + description + ":2   " + "f");

		assertEqual(input, expected, description);
	}

	private void assertEqual(List<String> input, List<String> expected, String description) throws IOException {
		String realInput = buildString(input);
		String realExpected = buildString(expected);

		String processedInput = convertToString(new LineWrapperInputStream(new ByteArrayInputStream(realInput.getBytes("UTF-8")), description));

		Assert.assertEquals(realExpected, processedInput);
	}

	private String convertToString(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileCopyUtils.copy(is, out);
		return out.toString("UTF-8");
	}

	private String buildString(List<String> expected) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> i = expected.iterator();
		while (i.hasNext()) {
			String s = i.next();
			sb.append(s);
			if (i.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
