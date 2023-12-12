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

	public SearchResultsServlet(ThreadSafeInvertedIndex index, QueryProcessorInterface queryProcessor)
			throws IOException {
		this.index = index;
		this.queryProcessor = queryProcessor;
		resultsTemplate = SearchEngine.readResourceFile("Results.html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String searchQuery = HtmlCleaner.stripHtml(request.getParameter("query"));
		String action = request.getParameter("action");

		Set<String> queryWords = convertQueryToWords(searchQuery);
		List<InvertedIndex.SearchResult> results = index.partialSearch(queryWords);

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
				resultsBuilder.append("<li>")
						.append("<a href=\"")
						.append(result.getWhere())
						.append("\">")
						.append(result.getWhere())
						.append("</a>")
						.append("</li>");
			}
			resultsBuilder.append("</ol>");
		}
		else {
			resultsBuilder.append("<strong><p>No results generated</p></strong>");
		}

		return resultsTemplate.replace("${searchQuery}", searchQuery).replace("${results}", resultsBuilder.toString());
	}
}
