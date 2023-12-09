package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SearchResultsServlet extends HttpServlet {

	private InvertedIndex index;
	private QueryProcessorInterface queryProcessor;

	public SearchResultsServlet(InvertedIndex index, QueryProcessorInterface queryProcessor) throws IOException {

		this.index = index;
		this.queryProcessor = queryProcessor;

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String searchQuery = request.getParameter("query");

		Set<String> queryWords = convertQueryToWords(searchQuery);
		// TODO check if need to do exact search
		List<InvertedIndex.SearchResult> results = index.partialSearch(queryWords);

		Map<String, List<InvertedIndex.SearchResult>> resultsMap = new HashMap<>();
		resultsMap.put(searchQuery, results);

		String jsonResults = JsonFormatter.writeSearchResultsToString(resultsMap);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print(jsonResults);
		out.flush();
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

}

//// Convert JSON string to formatted HTML
//// This method should transform the JSON into a human-readable HTML format
//String formattedHtml = JsonToHtmlFormatter.format(jsonResults);
