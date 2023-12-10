package edu.usfca.cs272;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InvertedIndexServlet extends HttpServlet {

	private InvertedIndex index;
	private QueryProcessorInterface queryProcessor;
	private final String indexTemplate;

	public InvertedIndexServlet(ThreadSafeInvertedIndex index, QueryProcessorInterface queryProcessor) throws IOException  {

		this.index = index;
		this.queryProcessor = queryProcessor;
		indexTemplate = readResourceFile("Index.html");

	}
	//TODO need to make tmeplate prettier and make links clickable

	//TODO duplicate code below
	/**
	 * Reads a file from the classpath and returns its content as a String.
	 *
	 * @param fileName The name of the file to be read, relative to the classpath.
	 * @return The content of the file as a String.
	 * @throws IOException If an error occurs during file reading.
	 */
	public String readResourceFile(String fileName) throws IOException {
		String resourcePath = "html/" + fileName;
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
		if (inputStream == null) {
			throw new FileNotFoundException("Resource file not found: " + resourcePath);
		}
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		System.out.println(index.toString());

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print("");
		out.flush();
	}
}
