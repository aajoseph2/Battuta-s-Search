package edu.usfca.cs272;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet for handling requests to clear the search history. This servlet calls
 * the clear method on the SearchHistory instance to reset the history.
 */
public class ClearHistoryServlet extends HttpServlet {

	/**
	 * Class version for serialization, in [YEAR][TERM] format (unused).
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instance of searchHistory class that provides functionality to add, retrieve,
	 * and clear the history of searches made by users. Ensures that the search
	 * history is maintained accurately across different servlet requests.
	 */
	private SearchHistory searchHistory;

	/**
	 * Constructs a ClearHistoryServlet with the given SearchHistory instance.
	 *
	 * @param searchHistory The SearchHistory instance used for managing search
	 *   history.
	 * @throws IOException throws exception if template is unreadable
	 */
	public ClearHistoryServlet(SearchHistory searchHistory) throws IOException {
		this.searchHistory = searchHistory;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		searchHistory.clearSearchHistory();
		response.sendRedirect("/history");
	}
}
