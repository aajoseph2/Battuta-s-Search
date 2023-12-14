package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Class provides the functionality to view and interact with the inverted
 * Index. utilizes the indexTemplate for the display of the html page. Also
 * stores the functionality to download json file of inverted index
 */
public class InvertedIndexServlet extends HttpServlet {

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
	 * Template used to display the inverted index.
	 */
	private final String indexTemplate;

	/**
	 * Constructs this class to contain the correct instance of the inverted index,
	 * containing all data.
	 *
	 * @param index A thread-safe version of the inverted index data structure.
	 * @throws IOException throws exception if template is unreadable
	 */
	public InvertedIndexServlet(ThreadSafeInvertedIndex index) throws IOException {
		this.index = index;
		indexTemplate = SearchEngine.readResourceFile("Index.html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print(buildHtmlIndex());
		out.flush();
	}

	/**
	 * This String is used to update the Inverted Index html page. using index-link
	 * to make links white when hovering over for better visibilty. Ï
	 *
	 * @return String of to update the necessary components of the InvertedIndex
	 *   html page.
	 */
	private String buildHtmlIndex() {
		StringBuilder builder = new StringBuilder();

		for (String word : new TreeSet<>(index.getWords())) {
			builder.append("<li>").append(word).append(":<ul style='margin-left: 25px;'>");
			for (String location : index.getLocations(word)) {
				int frequency = index.numWordFrequencyAtLocation(word, location);
				builder.append("<li>")
						.append("<a class='index-link' href=\"")
						.append(location)
						.append("\">-> ")
						.append(location)
						.append("</a> <p style=\"margin-left: 30px;\">-> Word Count - ")
						.append(frequency)
						.append("</p>")
						.append("</li>");
			}

			builder.append("</ul></li>");
		}

		return indexTemplate.replace("${results}", builder.toString());
	}

}
