package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Multithreaded version of QueryProcessor
 */
public class MultithreadedQueryProcessor implements QueryProcessorInterface {

	/**
	 * The lock used to protect concurrent access to the underlying set.
	 */
	private final MultiReaderLock lock;

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
	 * Workers being assigned to the search operations of query processor
	 */
	private final WorkQueue workers;

	/**
	 * Initializes the Query map with empty data structures.
	 *
	 * @param workers meant to be utilized to complete query process
	 * @param partial boolean for choosing the search method
	 * @param index thread safe class that contains the inverted index structure
	 */
	public MultithreadedQueryProcessor(WorkQueue workers, boolean partial, ThreadSafeInvertedIndex index) {
		this.query = new TreeMap<>();
		searchFunction = partial ? index::exactSearch : index::partialSearch;
		lock = new MultiReaderLock();
		this.workers = workers;

	}

	/**
	 * processes a query of words when given a file location. Processed line by line
	 * as reading line by line
	 *
	 * @param location Where the query is being retrieved from
	 * @throws IOException If file is unreadable
	 */
	@Override
	public void queryProcessor(Path location) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(location, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String finalLine = line;
				workers.execute(() -> {
					try {
						queryProcessor(finalLine);
					}
					catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
			}
		}
		workers.finish();
	}

	/**
	 * Helper to process query line by line.
	 *
	 * @param line the specific line of words intended to query
	 * @throws IOException If file is unreadable
	 */
	@Override
	public void queryProcessor(String line) throws IOException {
		var buffer = TextParser.uniqueStems(line);
		String processedQuery = String.join(" ", buffer);

		lock.writeLock().lock();
		try {
			if (!buffer.isEmpty() && !hasQuery(processedQuery)) {
				this.query.put(processedQuery, null);
			}
			else {
				return;
			}
		}
		finally {
			lock.writeLock().unlock();
		}

		List<ThreadSafeInvertedIndex.SearchResult> currentResults = searchFunction.apply(buffer);

		lock.writeLock().lock();
		try {
			this.query.put(processedQuery, currentResults);
		}
		finally {
			lock.writeLock().unlock();
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
		lock.readLock().lock();
		try {
			var buffer = TextParser.uniqueStems(queryLine);
			String processedQuery = String.join(" ", buffer);
			return Collections.unmodifiableList(query.getOrDefault(processedQuery, Collections.emptyList()));
		}
		finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Returns a set view of all the queries processed.
	 *
	 * @return Set of processed queries.
	 */
	@Override
	public Set<String> getQueryLines() {
		lock.readLock().lock();
		try {
			return Collections.unmodifiableSet(query.keySet());
		}
		finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Checks if a specific query exists in the query map.
	 *
	 * @param queryLine The query to check
	 * @return True if the query exists, false otherwise
	 */
	@Override
	public boolean hasQuery(String queryLine) {
		lock.readLock().lock();
		try {
			var buffer = TextParser.uniqueStems(queryLine);
			String processedQuery = String.join(" ", buffer);
			return query.containsKey(processedQuery);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Retrieves the number of processed queries.
	 *
	 * @return Number of queries in the map.
	 */
	@Override
	public int queryCount() {
		lock.readLock().lock();
		try {
			return query.size();
		}
		finally {
			lock.readLock().unlock();
		}
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
		lock.readLock().lock();
		try {
			JsonFormatter.writeSearchResultsToFile(this.query, path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return String.format("QueryProcessor [queries=%s]", queryCount());
		}
		finally {
			lock.readLock().unlock();
		}
	}
}
