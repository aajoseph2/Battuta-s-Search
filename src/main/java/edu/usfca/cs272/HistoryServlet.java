package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HistoryServlet extends HttpServlet {

	private String historyTemplate;

	public HistoryServlet() throws IOException {

		historyTemplate = SearchEngine.readResourceFile("History.html");

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String htmlResults = buildHistoryHtmlResponse(SearchResultsServlet.searchHistory);
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(htmlResults);
		out.flush();
	}

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
