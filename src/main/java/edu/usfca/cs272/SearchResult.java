package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/*
 * TODO The values of the members of this class are fully dependent on a
 * specific instance of a specific inverted index. In other words, different
 * inverted indexes (one generated from text files and another generated from
 * web pages) will generate different search results for the same query. When a
 * class is so fully dependent on another like this, we nest them! Make this a
 * nested class within inverted index. When you do that, should it be a static
 * nested class or should it be a non-static inner class?
 */

/**
 * Search object class that has count, score, and where. These objects will be
 * used as the values within the map structure
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
	private String where; // TODO make final

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
	 * TODO Fill in 
	 * 
	 * @param path file path to be outputted
	 * @param results the updated query structure to be translated into json
	 * @throws IOException if file is not able to written
	 */
	public static void writeQueryJson(Path path, Map<String, List<SearchResult>> results) throws IOException {
		Files.write(path, JsonFormatter.writeSearchResults(results).getBytes()); // TODO Not efficient! 
	}

	@Override
	public String toString() {
		return String.format("Where: %s, Count: %d, Score: %.4f", where, count, score);
	}
}
