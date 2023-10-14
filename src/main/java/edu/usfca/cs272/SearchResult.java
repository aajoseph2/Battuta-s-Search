package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Search object class that has count, score, and where. These objects will be
 * used as the values within the map structure
 *
 */
public class SearchResult implements Comparable<SearchResult> {
	/**
	 * Count of given word from a given file
	 */
	private int count;
	/**
	 * Score of given query
	 */
	private double score;
	/**
	 * Location of a query search
	 */
	private String where;

	/**
	 * @param where location of word query location
	 * @param count count of all words in the query, within the given "where"
	 * @param score score of count of words from the given query
	 */
	public SearchResult(String where, int count, double score) {
		this.where = where;
		this.count = count;
		this.score = score;
	}

	/**
	 * Structure of word query as the key, and the SearchResult as the value
	 */
	public static Map<String, List<SearchResult>> query = new TreeMap<>();
	/**
	 * Query line to be searched for witin a given file location
	 */
	public static List<TreeSet<String>> qWords = new ArrayList<>();

	/**
	 * @return word frequency
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return score of given query
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @return get location of the given query
	 */
	public String getWhere() {
		return where;
	}

	@Override
	public int compareTo(SearchResult other) {
		int scoreComparison = Double.compare(other.score, this.score);
		if (scoreComparison != 0) {
			return scoreComparison;
		}

		int countComparison = Integer.compare(other.count, this.count);
		if (countComparison != 0) {
			return countComparison;
		}

		return this.where.compareToIgnoreCase(other.where);
	}

	/**
	 * @param path file path to be outputted
	 * @param results the updated query structure to be translated into json
	 * @throws IOException if file is not able to written
	 */
	public static void writeQueryJson(Path path, Map<String, List<SearchResult>> results) throws IOException {
		Files.write(path, JsonFormatter.writeSearchResults(results).getBytes());
	}

	@Override
	public String toString() {
		return String.format("Where: %s, Count: %d, Score: %.4f", where, count, score);
	}
}