package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class SearchResult implements Comparable<SearchResult> {
	private int count;
	private double score;
	private String where;

	public SearchResult(String where, int count, double score) {
		this.where = where;
		this.count = count;
		this.score = score;
	}

	public static Map<String, List<SearchResult>> query = new TreeMap<>();
	public static List<TreeSet<String>> qWords = new ArrayList<>();

	public int getCount() {
		return count;
	}

	public double getScore() {
		return score;
	}

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

	public static void writeQueryJson(Path path, Map<String, List<SearchResult>> results) throws IOException {
		Files.write(path, JsonFormatter.writeSearchResults(results).getBytes());
	}

	public static Map<String, Object> searchResultToMap(SearchResult result) {
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
		Map<String, Object> map = new HashMap<>();
		map.put("where", result.getWhere());
		map.put("count", result.getCount());
		map.put("score", FORMATTER.format(result.getScore()));
		return map;
	}

	@Override
	public String toString() {
		return String.format("Where: %s, Count: %d, Score: %.4f", where, count, score);
	}
}
