package edu.usfca.cs272;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClearHistoryServlet extends HttpServlet {

	private SearchHistory searchHistory;

	public ClearHistoryServlet(SearchHistory searchHistory) throws IOException {
		this.searchHistory = searchHistory;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		searchHistory.clearSearchHistory();
		response.sendRedirect("/history");
	}
}
