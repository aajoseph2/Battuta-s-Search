package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Class to manage search history. It maintains a list of all search queries
 * made during the server session and provides methods to manipulate and access
 * the history.
 */
public class SearchResultsServlet extends HttpServlet {

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
	 * Template for the results page after searching for a query.
	 */
	private final String resultsTemplate;

	/**
	 * Instance of searchHistory class that provides functionality to add, retrieve,
	 * and clear the history of searches made by users. Ensures that the search
	 * history is maintained accurately across different servlet requests.
	 */
	private SearchHistory searchHistory;

	/**
	 * Constructs a SearchResultsServlet with the provided index, and search
	 * history.
	 *
	 * @param index The ThreadSafeInvertedIndex instance for search operations.
	 * @param searchHistory The SearchHistory instance for managing search history.
	 * @throws IOException throws exception if template is unreadable
	 */
	public SearchResultsServlet(ThreadSafeInvertedIndex index, SearchHistory searchHistory) throws IOException {
		this.index = index;
		resultsTemplate = SearchEngine.readResourceFile("Results.html");
		this.searchHistory = searchHistory;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String searchQuery = HtmlCleaner.stripHtml(request.getParameter("query"));

		searchHistory.addSearchedQuery(searchQuery);
		
		// TODO Not efficient
		QueryProcessorInterface queryProcessor = new QueryProcessor("on".equals(request.getParameter("exact")), index);

		queryProcessor.queryProcessor(searchQuery);
		var results = new ArrayList<>(queryProcessor.getQueryResults(searchQuery));
		
		/* TODO 
		var queries = TextParser.uniqueStems(request.getParameter("query"));
		var results = index.search(queries, "on".equals(request.getParameter("exact"));
		*/

		if ("on".equals(request.getParameter("reverse"))) {
			Collections.reverse(results);
		}

		if ("lucky".equals(request.getParameter("action")) && !results.isEmpty()) {
			response.sendRedirect(results.get(0).getWhere());
		}
		else {
			String htmlResults = buildResultsHtmlResponse(searchQuery, results);
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(htmlResults);
			out.flush();
		}
	}

	/**
	 * Constructs HTML content displaying search results. This method generates an
	 * ordered list of search results, each result being a hyperlink to the
	 * corresponding document. If no results are found, a message indicating this is
	 * displayed instead.
	 *
	 * @param searchQuery The query string that was searched for.
	 * @param results The list of search results to be displayed.
	 * @return A String containing HTML content for the search results.
	 */
	private String buildResultsHtmlResponse(String searchQuery, List<InvertedIndex.SearchResult> results) {
		StringBuilder resultsBuilder = new StringBuilder();

		if (!results.isEmpty()) {
			resultsBuilder.append("<ol>");
			for (InvertedIndex.SearchResult result : results) {
				resultsBuilder.append("<li>")
						.append("<a class='index-link' href=\"")
						.append(result.getWhere())
						.append("\">")
						.append(result.getWhere())
						.append("</a>")
						.append("</li>");
			}
			resultsBuilder.append("</ol>");
		}
		else {
			resultsBuilder.append("<p>No results generated</p>");
		}

		return resultsTemplate.replace("${searchQuery}", searchQuery).replace("${results}", resultsBuilder.toString());
	}
}
