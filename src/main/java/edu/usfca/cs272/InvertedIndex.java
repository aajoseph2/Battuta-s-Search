package edu.usfca.cs272;

import java.util.Collections;
import java.util.Set;
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
	 * @return the CountsInfo in the Driver class
	 */
	public TreeMap<String, Integer> getWordCounts() {
		return new TreeMap<>(Collections.unmodifiableSortedMap(counts));
	}

	/* TODO Do this instead 
	public SortedMap<String, Integer> getWordCounts() {
		return Collections.unmodifiableSortedMap(counts);
	}
	*/

	/**
	 * @return the formatMap in the Driver class
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getInvertedIndex() { // TODO Remove
		return index;
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
	 * getter method
	 *
	 * @param location : path key to get the word count in fileCountsInfo
	 * @return the word count of specific file.
	 */
	public Integer getWordCountForFile(String location) {
		return counts.getOrDefault(location, 0);
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

	@Override
	public String toString() {
		return "InvertedIndex{" + "counts=" + counts + ", index=" + index + '}';
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
	
	// TODO Still need some of these: https://github.com/usf-cs272-fall2023/project-aajoseph2/blob/a8c04e3ae129f8c0654e785ff6519cd7bc14c377/src/main/java/edu/usfca/cs272/InvertedIndex.java#L52-L53

	// TODO Move getWordCounts here (drag/drop methods in the outline)
	
	/**
	 * Returns a set of all the words in the index.
	 *
	 * @return a set of all words
	 */
	public Set<String> getWords() {
		return index.keySet(); // TODO Make unmodifiable
	}
	
	/* TODO
	public Set<String> getLocations(String word) --> if the word exists, index.get(word).keySet()
	public Set<Integer> getPositions(String word, String location)
	
	
	.. 1 num/size method per has method
	*/

	/* TODO 
	public void writeJson(Path path) throws ... { <-- driver calls this intead
		JsonFormatter.writeIndexJson(index, path);
	}
	*/
	
}
