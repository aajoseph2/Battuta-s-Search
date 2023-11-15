package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Interface for the query proccesor classes
 */
public interface QueryProcessorInterface {
	/**
	 * Processes a query from a given file location, with each line being treated as
	 * a separate query.
	 *
	 * @param location The file path from which queries are read.
	 * @throws IOException If there is an issue reading from the file.
	 */
	void queryProcessor(Path location) throws IOException;

	/**
	 * Processes a single line of text as a query.
	 *
	 * @param line A line of text that represents a query.
	 * @throws IOException If there is an issue in processing the query.
	 */
	void queryProcessor(String line) throws IOException;

	/**
	 * Returns the search results for a specific query.
	 *
	 * @param queryLine The query for which results are required.
	 * @return A list of search results for the specified query.
	 */
	List<InvertedIndex.SearchResult> getQueryResults(String queryLine);

	/**
	 * Returns a set of all processed query lines.
	 *
	 * @return A set of query strings that have been processed.
	 */
	Set<String> getQueryLines();

	/**
	 * Checks whether a specific query has already been processed.
	 *
	 * @param queryLine The query string to check.
	 * @return True if the query has been processed, false otherwise.
	 */
	boolean hasQuery(String queryLine);

	/**
	 * Retrieves the total number of queries processed.
	 *
	 * @return The number of processed queries.
	 */
	int queryCount();

	/**
	 * Writes the search results to a file in JSON format.
	 *
	 * @param path The file path where the results should be written.
	 * @throws IOException If there is an issue writing to the file.
	 */
	void writeQueryJson(Path path) throws IOException;

	/**
	 * Returns a string representation of the query processor, typically used for
	 * debugging.
	 *
	 * @return A string representation of the query processor.
	 */
	@Override
	String toString();
}
