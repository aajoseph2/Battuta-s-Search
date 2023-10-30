package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure class to contain all major data structures that utilizes
 * wrapper methods, to alter data recieved from file inputs.
 */
public class InvertedIndex {

	/**
	 * The final counts map that builds the file path and counts.
	 */
	private final TreeMap<String, Integer> counts;
	/**
	 * Final structure to contain the index information.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * Initializes the inverted index with empty data structures.
	 */
	public InvertedIndex() {
		counts = new TreeMap<>();
		index = new TreeMap<>();
	}

	/**
	 * This method processes data from the textProcess methods, where the data will
	 * be inputted into maps, which are used to build a final data structure to read
	 * the counts and index of the file contents.
	 *
	 * @param word stem to be added in invertMap
	 * @param location file name, to be added within nestMap
	 * @param num position in file
	 */
	public void addData(String word, String location, Integer num) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(location, new TreeSet<>());

		if (index.get(word).get(location).add(num)) {
			counts.put(location, counts.getOrDefault(location, 0) + 1);
		}
	}

	/**
	 * Checks if the word count exists for the given location.
	 *
	 * @param location the location (file) to check
	 * @return true if the count exists, false otherwise
	 */
	public boolean hasCount(String location) {
		return counts.containsKey(location);
	}

	/**
	 * Checks if the index contains the given word.
	 *
	 * @param word the word to check
	 * @return true if the word exists, false otherwise
	 */
	public boolean hasWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * Checks if the word is associated with the given location.
	 *
	 * @param word the word to check
	 * @param location the location (file) to check
	 * @return true if the word exists at the given location, false otherwise
	 */
	public boolean hasLocation(String word, String location) {
		return index.containsKey(word) && index.get(word).containsKey(location);
	}

	/**
	 * Checks if the word is associated with the given location and position.
	 *
	 * @param word the word to check
	 * @param location the location (file) to check
	 * @param position the position of the word in the file
	 * @return true if the word exists at the given location and position, false
	 *   otherwise
	 */
	public boolean hasPosition(String word, String location, int position) {
		return hasLocation(word, location) && index.get(word).get(location).contains(position);
	}

	/**
	 * Sorted map of word counts in Counts
	 *
	 * @return the CountsInfo in the Driver class
	 */
	public SortedMap<String, Integer> getWordCounts() {
		return Collections.unmodifiableSortedMap(counts);
	}

	/**
	 * Returns a set of all the words in the index.
	 *
	 * @return a set of all words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());

	}

	/**
	 * getter method
	 *
	 * @param location : path key to get the word count in fileCountsInfo
	 * @return the word count of specific file.
	 */
	public Integer numCountForFile(String location) {
		return counts.getOrDefault(location, 0);
	}

	/**
	 * Returns the number of times a word appears in the index.
	 *
	 * @param word the word to check
	 * @return number of times the word appears
	 */
	public int numWordFrequency(String word) {
		return hasWord(word) ? index.get(word).size() : 0;
	}

	/**
	 * Total word count in specified location
	 *
	 * @param location file location used as key
	 * @return count of total words
	 */
	public int numTotalWordsForLocation(String location) {
		return counts.getOrDefault(location, 0);
	}

	/**
	 * Returns the number of times a word appears in a specific location in the
	 * index.
	 *
	 * @param word the word to check
	 * @param location the location (file) to check
	 * @return number of times the word appears at the given location
	 */
	public int numWordFrequencyAtLocation(String word, String location) {
		return getPositions(word, location).size();
	}

	/**
	 * Returns a set of all the locations where the given word is found.
	 *
	 * @param word the word to retrieve locations for
	 * @return a set of locations, or an empty set if the word is not found
	 */
	public Set<String> getLocations(String word) {
		if (hasWord(word)) {
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * Returns a set of all the positions where the given word is found in the given
	 * location.
	 *
	 * @param word the word to retrieve positions for
	 * @param location the location to retrieve positions for
	 * @return a set of positions, or an empty set if the word is not found at the
	 *   given location
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (hasLocation(word, location)) {
			return Collections.unmodifiableSet(index.get(word).get(location));
		}
		return Collections.emptySet();
	}

	/**
	 * Writes JSON formatted data from the given inverted index to a file.
	 *
	 * @param path File to be written to
	 * @throws IOException If there's an issue writing to the file
	 */
	public void writeJson(Path path) throws IOException {
		JsonFormatter.writeIndexJson(index, path, 1);
	}

	/**
	 * Logic for determing prefix for partial search
	 *
	 * @param prefix The prefix string to search for.
	 * @return A set of words that start with the provided prefix.
	 */
	public Set<String> prefixSearch(String prefix) { // TODO Remove
		String endKey = prefix;

		if (!prefix.isEmpty()) {
			char lastChar = prefix.charAt(prefix.length() - 1);
			endKey = prefix.substring(0, prefix.length() - 1) + (char) (lastChar + 1);
		}
		return new TreeSet<>(index.subMap(prefix, endKey).keySet());
	}

	/**
	 * Performs a search based on the provided query and the search mode
	 *
	 * @param queryWords words in query line to be searched
	 * @param isExact A flag to determine if the search should be exact or partial.
	 * @return the search result
	 * @throws IOException If there is an error during searching.
	 */
	// TODO Set<String> queryWords (and exact and partial search)
	public List<SearchResult> search(TreeSet<String> queryWords, boolean isExact) throws IOException {
		if (isExact) {
			return exactSearch(queryWords);
		}
		else {
			return partialSearch(queryWords);
		}
	}

	/**
	 * Performs an exact search based on the provided set of query words. This
	 * method only looks for the exact word matches within the inverted index.
	 *
	 * @param queryWords The set of words intended for the exact search.
	 * @return A list of search results based on the exact matches.
	 * @throws IOException If there is an error during the search.
	 */
	private List<SearchResult> exactSearch(TreeSet<String> queryWords) throws IOException {
		/* TODO 
		Map<String, SearchResult> locationCounts = new HashMap<>();
		List<SearchResult> currentResults = new ArrayList<>();
		
		for (String word : queryWords) {
			var locations = index.get(word);
			if (locations != null) {
				for (var locEntry : locations.entrySet()) {
					String loc = locEntry.getKey();
					int frequency = locEntry.getValue().size();
					
					if (locationCounts.containsKey(loc)) {
						locationCounts.get(loc).updateCount(frequency);
					}
					else {
						SearchResult result = new SearchResult(loc, frequency, 0);
						locationCounts.put(loc, result);
						currentResults.add(result);
					}
				}
			}
		}
		
		Collections.sort(currentResults);
		return currentResults;
		*/
		
		Map<String, Integer> locationCounts = new HashMap<>();

		for (String word : queryWords) {
			if (index.containsKey(word)) {
				for (var locEntry : index.get(word).entrySet()) {
					String loc = locEntry.getKey();
					int frequency = locEntry.getValue().size();
					locationCounts.put(loc, locationCounts.getOrDefault(loc, 0) + frequency);
				}
			}
		}
		return compileResults(locationCounts);
	}

	/**
	 * Performs a partial search based on the provided set of query words. This
	 * method will return matches for words that start with any of the query words,
	 * using a prefix search.
	 *
	 * @param queryWords The set of words intended for the partial search.
	 * @return A list of search results based on partial matches.
	 * @throws IOException If there is an error during the search.
	 */
	private List<SearchResult> partialSearch(TreeSet<String> queryWords) throws IOException {
		Map<String, Integer> locationCounts = new HashMap<>();

		for (String word : queryWords) {
			/* TODO 
			var locations = index.tailMap(word);
			if locations != null, loop through entrySet
			*/
			Set<String> relevantWords = prefixSearch(word); // TODO 
			for (String relevantWord : relevantWords) {
				if (index.containsKey(relevantWord)) {
					for (var locEntry : index.get(relevantWord).entrySet()) {
						String loc = locEntry.getKey();
						int frequency = locEntry.getValue().size();
						locationCounts.put(loc, locationCounts.getOrDefault(loc, 0) + frequency);
					}
				}
			}
		}
		return compileResults(locationCounts);
	}

	/**
	 * Compiles the exacr and partial search methods
	 *
	 * @param locationCounts The map containing word count for each location.
	 * retrieving total word count.
	 * @return A sorted list of SearchResult objects.
	 */
	private List<SearchResult> compileResults(Map<String, Integer> locationCounts) { // TODO Remove
		List<SearchResult> currentResults = new ArrayList<>();

		for (var locEntry : locationCounts.entrySet()) {
			int totalWords = counts.getOrDefault(locEntry.getKey(), 0);
			double score = (double) locEntry.getValue() / totalWords;
			currentResults.add(new SearchResult(locEntry.getKey(), locEntry.getValue(), score));
		}

		Collections.sort(currentResults);
		return currentResults;
	}

	@Override
	public String toString() {
		return "InvertedIndex{" + "counts=" + counts + ", index=" + index + '}';
	}

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
		private final String where;

		/**
		 * Constructor for SearchResult class, describes the structure of data
		 *
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
		 * word count getter
		 *
		 * @return word frequency
		 */
		public int getCount() {
			return count;
		}

		/**
		 * Score of search getter
		 *
		 * @return score of given query
		 */
		public double getScore() {
			return score;
		}

		/**
		 * Retrieving where a word is stored
		 *
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

		@Override
		public String toString() {
			return String.format("Where: %s, Count: %d, Score: %.4f", where, count, score);
		}
	}

}
