package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	 * Represents the inverted index data structure used for storing and
	 * searching text data
	 */
	private final InvertedIndex mapMethods;
	/**
	 * Flag indicating the search mode
	 */
	private final boolean isExact;


	/**
	 * Initializes the Query map with empty data structures.
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
		String[] words = line.split(" ");
		var buffer = TextParser.uniqueStems(Arrays.toString(words));
		String processedQuery = String.join(" ", buffer);
		if (!buffer.isEmpty() && !hasQuery(processedQuery)) {
			List<InvertedIndex.SearchResult> currentResults = mapMethods.search(buffer, isExact);
			addQueryResults(processedQuery, currentResults);
		}
	}

	// Is this ok, for the getQueryMap?
	/**
	 * returns unmodifiable view of query map
	 *
	 * @return Unmodifiable map of queries and their results
	 */
	public Map<String, List<InvertedIndex.SearchResult>> getQueryMap() {
		return Collections.unmodifiableMap(query);
	}

	/**
	 * Checks if a specific query exists in the query map.
	 *
	 * @param str The query to check
	 * @return True if the query exists, false otherwise
	 */
	public boolean hasQuery(String str) {
		return query.containsKey(str);
	}

	/**
	 * Adds a new query and its corresponding search results to the map.
	 *
	 * @param str The processed query.
	 * @param results The search results associated with the query.
	 */
	private void addQueryResults(String str, List<InvertedIndex.SearchResult> results) {
		this.query.put(str, results);
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
	 * @param results the updated query structure to be translated into json
	 * @throws IOException if file is not able to written
	 */
	public void writeQueryJson(Path path, QueryProcessor results) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write(JsonFormatter.writeSearchResults(results.query));
		}
	}
}
