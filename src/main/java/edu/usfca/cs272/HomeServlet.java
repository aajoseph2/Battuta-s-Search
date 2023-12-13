package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This class serves at the starting point for any user using the search engine.
 * This page is the source for all other servlets.
 */
public class HomeServlet extends HttpServlet {
	/**
	 * Class version for serialization, in [YEAR][TERM] format (unused).
	 * */
	private static final long serialVersionUID = 1;

	/** The title to use for this webpage. */
	private static final String title = "Battuta's";

	/** Format used for all date output. */
	public static final String longDateFormat = "hh:mm a 'on' EEEE, MMMM dd yyyy";

	/** Used to format dates (already thread-safe). */
	public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(longDateFormat);

	/**
	 * Template that the home page utilizes. Based and dedicated to the great
	 * explorer Abu Abdullah Muhammad ibn Battutah.
	 */
	private final String homeTemplate;

	/**
	 * Initializes this search engine home page. Utilizes home template html page.
	 *
	 * @throws IOException if unable to read templates
	 */
	public HomeServlet() throws IOException {
		homeTemplate = SearchEngine.readResourceFile("Home.html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> values = new HashMap<>();
		values.put("title", title);
		values.put("thread", Thread.currentThread().getName());
		values.put("updated", dateFormatter.format(LocalDateTime.now()));

		values.put("method", "POST");
		values.put("action", request.getServletPath());

		StringSubstitutor replacer = new StringSubstitutor(values);
		String home = replacer.replace(homeTemplate);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		out.println(home);

		out.flush();
	}
}
