package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet for handling the display of search history. This servlet retrieves
 * the search history from the SearchHistory instance and renders it as an HTML
 * page.
 */
public class HistoryServlet extends HttpServlet {

	/**
	 * Class version for serialization, in [YEAR][TERM] format (unused).
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Template that displays the history page.
	 */
	private String historyTemplate;

	/**
	 * Instance of searchHistory class that provides functionality to add, retrieve,
	 * and clear the history of searches made by users. Ensures that the search
	 * history is maintained accurately across different servlet requests.
	 */
	private SearchHistory searchHistory;

	/**
	 * Constructs a HistoryServlet with the given SearchHistory instance.
	 *
	 * @param searchHistory The SearchHistory instance used for accessing search
	 *   history.
	 * @throws IOException throws exception if template is unreadable
	 */
	public HistoryServlet(SearchHistory searchHistory) throws IOException {
		historyTemplate = SearchEngine.readResourceFile("History.html");
		this.searchHistory = searchHistory;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String htmlResults = buildHistoryHtmlResponse(searchHistory.getSearchedList());
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(htmlResults);
		out.flush();
	}

	/**
	 * Creates an HTML representation of the search history. This method formats the
	 * provided search history as an ordered list in HTML. Each entry in the search
	 * history is presented as a list item. If the search history is empty or null,
	 * a message indicating the absence of history is displayed.
	 *
	 * @param searchHistory The list containing the history of search queries.
	 * @return A String representing the HTML content of the search history.
	 */
	private String buildHistoryHtmlResponse(List<String> searchHistory) {
		StringBuilder resultsBuilder = new StringBuilder();

		if (searchHistory != null && !searchHistory.isEmpty()) {
			resultsBuilder.append("<ol>");
			for (String result : searchHistory) {
				resultsBuilder.append("<li>").append(result).append("</li>");
			}
			resultsBuilder.append("</ol>");
		}
		else {
			resultsBuilder.append("<p>No search history available.</p>");
		}

		return historyTemplate.replace("${results}", resultsBuilder.toString());
	}
}
