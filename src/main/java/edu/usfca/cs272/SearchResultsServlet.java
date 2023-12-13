package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SearchResultsServlet extends HttpServlet {

	private InvertedIndex index;
	private QueryProcessorInterface queryProcessor;
	private final String resultsTemplate;
	private SearchHistory searchHistory;

	public SearchResultsServlet(ThreadSafeInvertedIndex index, QueryProcessorInterface queryProcessor,
			SearchHistory searchHistory) throws IOException {
		this.index = index;
		this.queryProcessor = queryProcessor;
		resultsTemplate = SearchEngine.readResourceFile("Results.html");
		this.searchHistory = searchHistory;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String searchQuery = HtmlCleaner.stripHtml(request.getParameter("query"));
		String action = request.getParameter("action");
		boolean exactSearch = "on".equals(request.getParameter("exact"));

		searchHistory.addSearchedQuery(searchQuery);

		QueryProcessorInterface queryProcessor;
		QueryProcessorInterface queryProcessorTwo = new QueryProcessor(exactSearch, index);

		queryProcessorTwo.queryProcessor(searchQuery);

		for (var result : queryProcessorTwo.getQueryResults(searchQuery)) {
			System.out.println("Query Result: " + result.getWhere());
			System.out.println("Query Score: " + result.getScore());
			System.out.println("Query Count: " + result.getCount());
			System.out.println();
		}

		var results = queryProcessorTwo.getQueryResults(searchQuery);

		if ("lucky".equals(action) && !results.isEmpty()) {
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
				System.out.println("result Count: " + result.getCount());
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
