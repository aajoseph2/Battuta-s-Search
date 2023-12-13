package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SearchResultsServlet extends HttpServlet {

	private InvertedIndex index;
	private final String resultsTemplate;
	private SearchHistory searchHistory;

	public SearchResultsServlet(ThreadSafeInvertedIndex index, SearchHistory searchHistory) throws IOException {
		this.index = index;
		resultsTemplate = SearchEngine.readResourceFile("Results.html");
		this.searchHistory = searchHistory;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String searchQuery = HtmlCleaner.stripHtml(request.getParameter("query"));

		searchHistory.addSearchedQuery(searchQuery);
		QueryProcessorInterface queryProcessor = new QueryProcessor("on".equals(request.getParameter("exact")), index);

		queryProcessor.queryProcessor(searchQuery);
		var results = queryProcessor.getQueryResults(searchQuery);

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
