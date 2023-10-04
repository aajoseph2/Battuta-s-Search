package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
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
	 * The final -counts map that builds the file path and counts.
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
	 * @return a copy of the index. ensures encapsulation
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> constructIndexRepresentation() {
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> representation = new TreeMap<>();

		for (String word : getWords()) {
			TreeMap<String, TreeSet<Integer>> innerMap = new TreeMap<>();

			for (String location : getLocations(word)) {
				TreeSet<Integer> positions = new TreeSet<>(getPositions(word, location));
				innerMap.put(location, positions);
			}

			representation.put(word, innerMap);
		}

		return representation;
	}

	/**
	 * wrapper add method
	 *
	 * @param location File name to stored as key
	 * @param count words counts within the fn, for the value
	 */
	public void addWordCount(String location, Integer count) {
		counts.put(location, count);
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
		index.get(word).get(location).add(num);
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
	public Integer getWordCountForFile(String location) {
		return counts.getOrDefault(location, 0);
	}

	/**
	 * Returns the number of times a word appears in the index.
	 *
	 * @param word the word to check
	 * @return number of times the word appears
	 */
	public int getWordFrequency(String word) {
		return hasWord(word) ? index.get(word).size() : 0;
	}

	/**
	 * Returns the number of times a word appears in a specific location in the
	 * index.
	 *
	 * @param word the word to check
	 * @param location the location (file) to check
	 * @return number of times the word appears at the given location
	 */
	public int getWordFrequencyAtLocation(String word, String location) {
		return hasLocation(word, location) ? index.get(word).get(location).size() : 0;
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
	 * @param path File to be written to
	 * @param index Data to be used in file write
	 * @throws IOException If file is not able to be written
	 */
	public static void writeJson(Path path, InvertedIndex index) throws IOException {
		Files.write(path, JsonFormatter.writeIndexJson(index).getBytes());
	}

	@Override
	public String toString() {
		return "InvertedIndex{" + "counts=" + counts + ", index=" + index + '}';
	}

}
