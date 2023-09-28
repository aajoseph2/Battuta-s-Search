package edu.usfca.cs272;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
	// TODO private final TreeMap<String, Integer> counts;
	private TreeMap<Path, Integer> counts = new TreeMap<>();
	/**
	 * Final structure to contain the index information.
	 */
	// TODO private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private TreeMap<String, TreeMap<String, List<Integer>>> index = new TreeMap<>();

	/* TODO 
	public InvertedIndex() {
		counts = new TreeMap<>();
		index = new TreeMap<>();
	}
	*/

	/**
	 * @return the CountsInfo in the Driver class
	 */
	public TreeMap<Path, Integer> getWordCounts() {
		return counts; 
		// TODO return Collections.unmodifiableSortedMap(counts);
	}
	/**
	 * @return the formatMap in the Driver class
	 */
	public TreeMap<String, TreeMap<String, List<Integer>>> getInvertedIndex() {
		return index;
	}
	
	/*
	 * TODO 
	 * toString
	 * 
	 * boolean has/contains
	 * int num/size
	 * various non-nested get/view methods
	 * 
	 * boolean hasCount(String location) --> counts.containsKey(location)
	 * boolean hasWord(String word)
	 * boolean hasLocation(String word, String location)
	 * boolean hasPosition(String word, String location, int position)
	 * 
	 * ...
	 * getWords() --> keyset from index
	 */
	
	/**
	 * wrapper add method
	 * @param fn File name to stored as key
	 * @param val words counts within the fn, for the value
	 */
	public void addWordCount(Path fn, Integer val) { // TODO (String location, Integer count)
		counts.put(fn, val);
	}
	
	/**
	 * wrapper add method
	 * @param str Stem of word to be used as key
	 * @param val The nested data structure - nestMap
	 */
	public void addIndexedWord(String str, TreeMap<String, List<Integer>> val) { // TODO Remove
		index.put(str, val);
	}
	
	/**
	 * getter method
	 * @param fn : path key to get the word count in fileCountsInfo
	 * @return the word count of specific file.
	 */
	public Integer getWordCountForFile(Path fn) {
		return counts.get(fn); // TODO getOrDefault
	}
	
	/**
	 * getter method
	 * @param str uses the stem to find the asscoiated nested data of file name along with
	 * word position
	 * @return either an empty nested map or the nested map with index info
	 */
	public TreeMap<String, List<Integer>> getIndexedWordPositions(String str) { // TODO Remove integrate into the code that uses it
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
	// TODO public void addData(String word, String location, Integer num) {
	public void addData(String stem, String fn, Integer num, InvertedIndex mapMethods) {
		// TODO Access the index here instead
		/*
		 * TODO 
		 * 1) Most efficient is to combine get() and checking for null values to reduce
		 * the number of times you access the data structure
		 * 
		 * 2) Most compact is 3 lines of code using putIfAbsent and repeated get calls
		 */
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
	public void clearAll() { // TODO Remove
		counts.clear();
		index.clear();
	}
}
