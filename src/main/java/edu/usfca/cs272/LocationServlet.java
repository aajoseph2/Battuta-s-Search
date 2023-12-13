package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedMap;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet for handling requests to retreive location of words in inverted
 * index.
 */
public class LocationServlet extends HttpServlet {

	/**
	 * Class version for serialization, in [YEAR][TERM] format (unused).
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A thread-safe version of the inverted index data structure. Stores words and
	 * their associated positions. Final structure to contain the index information.
	 */
	private InvertedIndex index;

	/**
	 * Template responsible for the location page of all links
	 */
	private final String locationTemplate;

	/**
	 * Constructs a LocationServlet with the given SearchHistory instance.
	 *
	 * @param index A thread-safe version of the inverted index data structure.
	 * @throws IOException throws exception if template is unreadable
	 */
	public LocationServlet(ThreadSafeInvertedIndex index) throws IOException {
		this.index = index;
		locationTemplate = SearchEngine.readResourceFile("Locations.html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print(buildHtmlLocations());
		out.flush();
	}

	/**
	 * Constructs HTML content displaying search locations. This method generates an
	 * list of locations, each result being a hyperlink to the corresponding
	 * website.
	 *
	 * @return A String containing HTML content for the search results.
	 */
	private String buildHtmlLocations() {
		StringBuilder builder = new StringBuilder();
		SortedMap<String, Integer> wordCounts = index.getWordCounts();

		builder.append("<ol>");

		for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
			String location = entry.getKey();
			Integer count = entry.getValue();

			builder.append("<li><a class='index-link' href=\"")
					.append(location)
					.append("\">")
					.append(location)
					.append("</a> - Word Count: ")
					.append(count)
					.append("</li>");
		}

		builder.append("</ol>");

		return locationTemplate.replace("${title}", "Battuta's Search - Locations")
				.replace("${results}", builder.toString());
	}

}
