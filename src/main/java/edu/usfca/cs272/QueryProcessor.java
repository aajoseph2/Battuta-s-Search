package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Processes queries that are used to as the worsd to be searched for from the
 * given indexed structures
 */
public class QueryProcessor implements QueryProcessorInterface{

	/**
	 * Structure of word query as the key, and the SearchResult as the value
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> query;
	/**
	 * Represents the inverted index data structure used for storing and searching
	 * text data
	 */
	/**
	 * function indicating the search mode
	 */
	private final Function<Set<String>, List<InvertedIndex.SearchResult>> searchFunction;
	/**
	 * Intended to stem text
	 */
	private final Stemmer stemmer;

	/**
	 * Initializes the Query map with empty data structures.
	 *
	 * @param partial boolean for choosing the search method
	 * @param index class that contains the inverted index structure
	 */
	public QueryProcessor(boolean partial, InvertedIndex index) {
		this.query = new TreeMap<>();
		this.stemmer = new SnowballStemmer(ENGLISH);
		searchFunction = partial ? index::exactSearch : index::partialSearch;
	}

	/**
	 * Helper to process query line by line.
	 *
	 * @param line the specific line of words intended to query
	 * @throws IOException If file is unreadable
	 */
	@Override
	public void queryProcessor(String line) throws IOException {

		var buffer = TextParser.uniqueStems(line, stemmer);
		String processedQuery = String.join(" ", buffer);

		if (!buffer.isEmpty() && !hasQuery(processedQuery)) {
			List<InvertedIndex.SearchResult> currentResults = searchFunction.apply(buffer);
			this.query.put(processedQuery, currentResults);
		}
	}

	/**
	 * Returns the search results for a specific query.
	 *
	 * @param queryLine The query to get results for.
	 * @return Unmodifiable list of search results for the given query.
	 */
	@Override
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
	@Override
	public Set<String> getQueryLines() {
		return Collections.unmodifiableSet(query.keySet());
	}

	/**
	 * Checks if a specific query exists in the query map.
	 *
	 * @param queryLine The query to check
	 * @return True if the query exists, false otherwise
	 */
	@Override
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
	@Override
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
	@Override
	public void writeQueryJson(Path path) throws IOException {
		JsonFormatter.writeSearchResultsToFile(this.query, path);
	}

	@Override
	public String toString() {
		return String.format("QueryProcessor [queries=%s]", queryCount());
	}
}
