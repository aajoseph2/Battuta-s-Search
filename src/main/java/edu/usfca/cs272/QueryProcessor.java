package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Processes queries that are used to as the worsd to be searched for from the
 * given indexed structures
 */
public class QueryProcessor {

	/**
	 * Structure of word query as the key, and the SearchResult as the value
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> query;
	/**
	 * Represents the inverted index data structure used for storing and searching
	 * text data
	 */
	private final InvertedIndex mapMethods;
	/**
	 * Flag indicating the search mode
	 */
	private final boolean isExact;

	/**
	 * Intended to stem text
	 */
	private final Stemmer stemmer = new SnowballStemmer(ENGLISH);

	/**
	 * Initializes the Query map with empty data structures.
	 *
	 * @param mapMethods Inverted index data structure
	 * @param isExact Flag indicating the search mode
	 */
	public QueryProcessor(InvertedIndex mapMethods, boolean isExact) {
		this.query = new TreeMap<>();
		this.mapMethods = mapMethods;
		this.isExact = isExact;
	}

	/**
	 * @param location Where the query is being retrieved from
	 * @throws IOException If file is unreadable
	 */
	public void queryProcessor(Path location) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(location, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				queryProcessor(line);
			}
		}
	}

	/**
	 * Helper to process query line by line.
	 *
	 * @param line the specific line of words intended to query
	 * @throws IOException If file is unreadable
	 */
	public void queryProcessor(String line) throws IOException {

		var buffer = TextParser.uniqueStems(line, stemmer);
		String processedQuery = String.join(" ", buffer);

		if (!buffer.isEmpty() && !hasQuery(processedQuery)) {
			List<InvertedIndex.SearchResult> currentResults = mapMethods.search(buffer, isExact);
			this.query.put(processedQuery, currentResults);
		}
	}

	/**
	 * returns unmodifiable view of query map
	 *
	 * @return Unmodifiable map of queries and their results
	 */
	public Map<String, List<InvertedIndex.SearchResult>> getQueryMap() {
		Map<String, List<InvertedIndex.SearchResult>> resultMap = new HashMap<>();
		for (String queryLine : getQueryLines()) {
			resultMap.put(queryLine, getQueryResults(queryLine));
		}
		return Collections.unmodifiableMap(resultMap);
	}

	/**
	 * Returns the search results for a specific query.
	 *
	 * @param queryLine The query to get results for.
	 * @return Unmodifiable list of search results for the given query.
	 */
	public List<InvertedIndex.SearchResult> getQueryResults(String queryLine) {
		var buffer = TextParser.uniqueStems(queryLine, stemmer);
		String processedQuery = String.join(" ", buffer);

		return Collections.unmodifiableList(query.getOrDefault(processedQuery, Collections.emptyList()));
	}

	/**
	 * Returns a set view of all the queries processed.
	 *
	 * @return Set of processed queries.
	 */
	public Set<String> getQueryLines() {
		return Collections.unmodifiableSet(query.keySet());
	}

	/**
	 * Checks if a specific query exists in the query map.
	 *
	 * @param queryLine The query to check
	 * @return True if the query exists, false otherwise
	 */
	public boolean hasQuery(String queryLine) {
		var buffer = TextParser.uniqueStems(queryLine, stemmer);
		String processedQuery = String.join(" ", buffer);
		return query.containsKey(processedQuery);
	}

	/**
	 * Retrieves the number of processed queries.
	 *
	 * @return Number of queries in the map.
	 */
	public int queryCount() {
		return query.size();
	}

	/**
	 * Writes the provided search results to a file in JSON format.Takes map of
	 * query strings corresponding search results and converts the structure into a
	 * json formatted string.
	 *
	 * @param path file path to be outputted
	 * @throws IOException if file is not able to written
	 */
	public void writeQueryJson(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write(JsonFormatter.writeSearchResultsToString(this.query));
		}
	}

	@Override
	public String toString() {
		return String.format("QueryProcessor [queries=%s, exact mode=%b]", queryCount(), isExact);
	}
}
