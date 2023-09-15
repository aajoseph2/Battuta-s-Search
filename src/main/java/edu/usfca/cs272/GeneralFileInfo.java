package edu.usfca.cs272;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Data structure class to contain all major data structures that utilizes wrapper methods, to
 * alter data recieved from file inputs.
 */
public class GeneralFileInfo {

	/**
	 * The final -counts map that builds the file path and counts.
	 */
	private TreeMap<Path, Integer> fileCountsInfo = new TreeMap<>();
	/**
	 * Nested map to be inputted as the value of both invertMap and formatMap.
	 */
	private TreeMap<String, List<Integer>> nestMap = new TreeMap<>();
	/**
	 * Final structure to contain the index information.
	 */
	private TreeMap<String, TreeMap<String, List<Integer>>> formatMap = new TreeMap<>();

	/**
	 * @return the fileCountsInfo in the Driver class
	 */
	public TreeMap<Path, Integer> getFileCountsInfo() {
		return fileCountsInfo;
	}

	/**
	 * @return the nestMap in the Driver class
	 */
	public TreeMap<String, List<Integer>> getNestMap() {
		return nestMap;
	}

	/**
	 * @return the formatMap in the Driver class
	 */
	public TreeMap<String, TreeMap<String, List<Integer>>> getFormatMap() {
		return formatMap;
	}

	/**
	 * wrapper add method
	 * @param fn File name to stored as key
	 * @param val words counts within the fn, for the value
	 */
	public void addFileCountsInfo(Path fn, Integer val) {
		fileCountsInfo.put(fn, val);
	}

	/** wrapper add method
	 * @param str File name to be stored as key
	 * @param val the Position of where that word is located within
	 * the file "str"
	 */
	public void addNestInfo(String str, List<Integer> val) {
		nestMap.put(str, val);
	}


	/**
	 * wrapper add method
	 * @param str Stem of word to be used as key
	 * @param val The nested data structure - nestMap
	 */
	public void addFormatInfo(String str, TreeMap<String, List<Integer>> val) {
		formatMap.put(str, val);
	}

	/**
	 * getter method
	 * @param fn : path key to get the word count in fileCountsInfo
	 * @return the word count of specific file.
	 */
	public Integer getFileInfoVal(Path fn) {
		return fileCountsInfo.get(fn);
	}

	/**
	 * getter method that etither returns the word postiton
	 * array list or an empty array list
	 * @param str this is the file name or "key" asscoiated with the word postions.
	 * @return either an empty array list or the array list of word positions
	 */
	public List<Integer> getNestVal(String str) {
		return nestMap.getOrDefault(str, new ArrayList<>());
	}


	/**
	 * getter method
	 * @param str uses the stem to find the asscoiated nested data of file name along with
	 * word position
	 * @return either an empty nested map or the nested map with index info
	 */
	public TreeMap<String, List<Integer>> getFormatVal(String str) {
		return formatMap.getOrDefault(str, new TreeMap<>());
	}

	/**
	 * clears all data structures before entering the testProcess engine,
	 * to make sure that no data is being overwritten with the wrong
	 * contents.
	 */
	public void clearAll() {
		fileCountsInfo.clear();
		nestMap.clear();
		formatMap.clear();
	}
}
