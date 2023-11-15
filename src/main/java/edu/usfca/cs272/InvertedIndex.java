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
	 * Merges all the data from a local inverted index to this shared inverted index.
	 *
	 * @param localIndex the local inverted index to add
	 */
	public void addAll(InvertedIndex localIndex) {
		for (var localOuter : localIndex.index.entrySet()) {
			String localWord = localOuter.getKey();
			var localInner = localOuter.getValue();

			if (!this.index.containsKey(localWord)) {
				this.index.put(localWord, localInner);
			}
			else {
				for (var localLocationEntry : localInner.entrySet()) {
					String localLocation = localLocationEntry.getKey();
					TreeSet<Integer> localPositions = localLocationEntry.getValue();
					this.index.get(localWord).putIfAbsent(localLocation, new TreeSet<>());
					this.index.get(localWord).get(localLocation).addAll(localPositions);
				}
			}
		}

		for (var localCountEntry : localIndex.counts.entrySet()) {
			String location = localCountEntry.getKey();
			Integer count = localCountEntry.getValue();
			this.counts.put(location, this.counts.getOrDefault(location, 0) + count);
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
	 * Performs a search based on the provided query and the search mode
	 *
	 * @param queryWords words in query line to be searched
	 * @param isExact A flag to determine if the search should be exact or partial.
	 * @return the search result
	 * @throws IOException If there is an error during searching.
	 */
	public List<SearchResult> search(Set<String> queryWords, boolean isExact) throws IOException {
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
	 */
	public List<SearchResult> exactSearch(Set<String> queryWords) {
		Map<String, SearchResult> searchResults = new HashMap<>();
		List<SearchResult> currentResults = new ArrayList<>();

		for (String word : queryWords) {
			var words = index.get(word);
			if (words != null) {
				compileResults(words, searchResults, currentResults);
			}
		}
		return currentResults;
	}

	/**
	 * Performs a partial search based on the provided set of query words. This
	 * method will return matches for words that start with any of the query words,
	 * using a prefix search.
	 *
	 * @param queries The set of words intended for the partial search.
	 * @return A list of search results based on partial matches.
	 */
	public List<SearchResult> partialSearch(Set<String> queries) {
		Map<String, SearchResult> searchResults = new HashMap<>();
		List<SearchResult> currentResults = new ArrayList<>();

		for (String query : queries) {
			var words = index.tailMap(query);
			if (words != null) {
				for (var wordEntry : words.entrySet()) {
					String word = wordEntry.getKey();
					if (!word.startsWith(query))
						break;
					compileResults(wordEntry.getValue(), searchResults, currentResults);
				}
			}
		}
		return currentResults;
	}

	/**
	 * @param locations locations where words were found.
	 * @param resultLookup map used to look up and/or store SearchResult objects.
	 * @param searchResults list to store and eventually return the search results.
	 */
	private void compileResults(TreeMap<String, TreeSet<Integer>> locations, Map<String, SearchResult> resultLookup,
			List<SearchResult> searchResults) {

		for (var locEntry : locations.entrySet()) {
			String loc = locEntry.getKey();
			int frequency = locEntry.getValue().size();
			SearchResult result = resultLookup.get(loc);
			if (result == null) {
				result = new SearchResult(loc);
				resultLookup.put(loc, result);
				searchResults.add(result);
			}
			result.updateCount(frequency);
		}
		Collections.sort(searchResults);
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
		 */
		public SearchResult(String where) {
			this.where = where;
			this.count = 0;
			this.score = 0;
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

		/**
		 * Updates the count of the SearchResult with an additional count.
		 *
		 * @param additionalCount The count to be added to the existing count.
		 */
		private void updateCount(int additionalCount) {
			this.count += additionalCount;
			this.score = (double) this.count / counts.get(where);
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
