package edu.usfca.cs272;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GeneralFileInfo {

	private TreeMap<Path, Integer> fileCountsInfo = new TreeMap<>();
	private TreeMap<String, List<Integer>> nestMap = new TreeMap<>();
	private Map<String, TreeMap<String, List<Integer>>> invertMap = new HashMap<>();
	private TreeMap<String, TreeMap<String, List<Integer>>> formatMap = new TreeMap<>();

	public TreeMap<Path, Integer> getFileCountsInfo() {
		return fileCountsInfo;
	}

	public TreeMap<String, List<Integer>> getNestMap() {
		return nestMap;
	}

	public Map<String, TreeMap<String, List<Integer>>> getInvertMap() {
		return invertMap;
	}

	public TreeMap<String, TreeMap<String, List<Integer>>> getFormatMap() {
		return formatMap;
	}

	public void addFileCountsInfo(Path fn, Integer val) {
		fileCountsInfo.put(fn, val);
	}

	public void addNestInfo(String str, List<Integer> val) {
		nestMap.put(str, val);
	}

	public void addInvertedInfo(String str, TreeMap<String, List<Integer>> val) {
		invertMap.put(str, val);
	}

	public void addFormatInfo(String str, TreeMap<String, List<Integer>> val) {
		formatMap.put(str, val);
	}

	public Integer getFileInfoVal(Path fn) {
		return fileCountsInfo.get(fn);
	}

	public List<Integer> getNestVal(String str) {
		return nestMap.getOrDefault(str, new ArrayList<>());
	}

	public TreeMap<String, List<Integer>> getInvertedVal(String str) {
		return invertMap.getOrDefault(str, new TreeMap<>());
	}

	public TreeMap<String, List<Integer>> getFormatVal(String str) {
		return formatMap.get(str);
	}

	public void clearAll() {
		fileCountsInfo.clear();
		nestMap.clear();
		invertMap.clear();
		formatMap.clear();
	}
}
