package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

		queryProcessor.queryProcessor(searchQuery);

		for (var result : queryProcessor.getQueryResults(searchQuery)) {
			System.out.println("Query Result: " + result.getWhere());
			System.out.println("Query Score: " + result.getScore());
			System.out.println("Query Count: " + result.getCount());
			System.out.println();
		}
		//System.out.println("Query line: " + queryProcessor.getQueryLines());
		//System.out.println("Query Results: " + searchHistory.toString());

		Set<String> queryWords = convertQueryToWords(searchQuery);
		List<InvertedIndex.SearchResult> results = index.search(queryWords, exactSearch);

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

	private Set<String> convertQueryToWords(String searchQuery) {
		Set<String> words = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		if (searchQuery == null || searchQuery.isBlank()) {
			return words;
		}

		String[] splitQuery = searchQuery.trim().split("\\s+");
		for (String word : splitQuery) {
			word = word.toLowerCase();
			words.add(word);
		}

		return words;
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
