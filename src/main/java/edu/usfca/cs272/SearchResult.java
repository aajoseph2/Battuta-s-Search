package edu.usfca.cs272;

public class SearchResult implements Comparable<SearchResult> {
	private int count;
	private double score;
	private String where;

	public SearchResult(String where, int count, double score) {
		this.where = where;
		this.count = count;
		this.score = score;
	}

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

	@Override
	public String toString() {
		return String.format("Where: %s, Count: %d, Score: %.4f", where, count, score);
	}
}
