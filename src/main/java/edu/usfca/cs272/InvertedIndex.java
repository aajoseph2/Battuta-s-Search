package edu.usfca.cs272;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Data structure class to contain all major data structures that utilizes wrapper methods, to
 * alter data recieved from file inputs.
 */
public class InvertedIndex {

	/**
	 * The final -counts map that builds the file path and counts.
	 */
	private TreeMap<Path, Integer> counts = new TreeMap<>();
	/**
	 * Final structure to contain the index information.
	 */
	private TreeMap<String, TreeMap<String, List<Integer>>> index = new TreeMap<>();


	/**
	 * @return the CountsInfo in the Driver class
	 */
	public TreeMap<Path, Integer> getWordCounts() {
		return counts;
	}
	/**
	 * @return the formatMap in the Driver class
	 */
	public TreeMap<String, TreeMap<String, List<Integer>>> getInvertedIndex() {
		return index;
	}
	/**
	 * wrapper add method
	 * @param fn File name to stored as key
	 * @param val words counts within the fn, for the value
	 */
	public void addWordCount(Path fn, Integer val) {
		counts.put(fn, val);
	}
	/**
	 * wrapper add method
	 * @param str Stem of word to be used as key
	 * @param val The nested data structure - nestMap
	 */
	public void addIndexedWord(String str, TreeMap<String, List<Integer>> val) {
		index.put(str, val);
	}
	/**
	 * getter method
	 * @param fn : path key to get the word count in fileCountsInfo
	 * @return the word count of specific file.
	 */
	public Integer getWordCountForFile(Path fn) {
		return counts.get(fn);
	}
	/**
	 * getter method
	 * @param str uses the stem to find the asscoiated nested data of file name along with
	 * word position
	 * @return either an empty nested map or the nested map with index info
	 */
	public TreeMap<String, List<Integer>> getIndexedWordPositions(String str) {
		return index.getOrDefault(str, new TreeMap<>());
	}
	/**
	 * This method processes data from the textProcess methods, where the data will
	 * be inputted into maps, which are used to build a final data structure to read
	 * the counts and index of the file contents.
	 * @param stem stem to be added in  invertMap
	 * @param fn file name, to be added within nestMap
	 * @param num position in file
	 * @param mapMethods contains the structure for the read data
	 */
	public void addData(String stem, String fn, Integer num, InvertedIndex mapMethods) {

		TreeMap<String, List<Integer>> nestMap = new TreeMap<>();

		nestMap = mapMethods.getIndexedWordPositions(stem);
		List<Integer> positionsList = nestMap.getOrDefault(fn, new ArrayList<>());
		positionsList.add(num);

		nestMap.put(fn, positionsList);
		mapMethods.addIndexedWord(stem, nestMap);
}
	/**
	 * clears all data structures before entering the testProcess engine,
	 * to make sure that no data is being overwritten with the wrong
	 * contents.
	 */
	public void clearAll() {
		counts.clear();
		index.clear();
	}
}
