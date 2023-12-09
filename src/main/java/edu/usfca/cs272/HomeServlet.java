package edu.usfca.cs272;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class HomeServlet extends HttpServlet {
	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202308;

	/** The title to use for this webpage. */
	private static final String title = "Search Engine";

	/** The data structure to use for storing messages. */
	private final LinkedList<MessageServlet.Message> messages;

	/** Template for starting HTML (including <head> tag). **/
	private final String headTemplate;

	/** Template for ending HTML (including <foot> tag). **/
	private final String footTemplate;

	/**
	 * Initializes this message board. Each message board has its own collection of
	 * messages.
	 *
	 * @throws IOException if unable to read templates
	 */
	public HomeServlet() throws IOException {
		super();
		messages = new LinkedList<>();

		headTemplate = readResourceFile("Home-head.html");
		footTemplate = readResourceFile("Home-foot.html");
	}

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

			Map<String, String> values = new HashMap<>();
			values.put("title", title);
			values.put("thread", Thread.currentThread().getName());
			values.put("updated", MessageServlet.dateFormatter.format(LocalDateTime.now()));

			values.put("method", "POST");
			values.put("action", request.getServletPath());

			StringSubstitutor replacer = new StringSubstitutor(values);
			String head = replacer.replace(headTemplate);
			String foot = replacer.replace(footTemplate);

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = response.getWriter();
			out.println(head);

			out.println(foot);
			out.flush();
	}
}
