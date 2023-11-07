package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Thread safe version of InvertedIndex
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/*
	 * The lock used to protect concurrent access to the underlying set.
	 */
	private final MultiReaderLock lock;

	/**
	 * Initializes the inverted index with empty data structures.
	 */
	public ThreadSafeInvertedIndex() {
		lock = new MultiReaderLock();
	}

	/**
	 * Returns the identity hashcode of the lock object. Not particularly useful.
	 *
	 * @return the identity hashcode of the lock object
	 */
	public int lockCode() {
		return System.identityHashCode(lock);
	}

	@Override
	public void addData(String word, String location, Integer num) {
		// TODO Auto-generated method stub
		super.addData(word, location, num);
	}

	@Override
	public boolean hasCount(String location) {
		// TODO Auto-generated method stub
		return super.hasCount(location);
	}

	@Override
	public boolean hasWord(String word) {
		// TODO Auto-generated method stub
		return super.hasWord(word);
	}

	@Override
	public boolean hasLocation(String word, String location) {
		// TODO Auto-generated method stub
		return super.hasLocation(word, location);
	}

	@Override
	public boolean hasPosition(String word, String location, int position) {
		// TODO Auto-generated method stub
		return super.hasPosition(word, location, position);
	}

	@Override
	public SortedMap<String, Integer> getWordCounts() {
		// TODO Auto-generated method stub
		return super.getWordCounts();
	}

	@Override
	public Set<String> getWords() {
		// TODO Auto-generated method stub
		return super.getWords();
	}

	@Override
	public Integer numCountForFile(String location) {
		// TODO Auto-generated method stub
		return super.numCountForFile(location);
	}

	@Override
	public int numWordFrequency(String word) {
		// TODO Auto-generated method stub
		return super.numWordFrequency(word);
	}

	@Override
	public int numTotalWordsForLocation(String location) {
		// TODO Auto-generated method stub
		return super.numTotalWordsForLocation(location);
	}

	@Override
	public int numWordFrequencyAtLocation(String word, String location) {
		// TODO Auto-generated method stub
		return super.numWordFrequencyAtLocation(word, location);
	}

	@Override
	public Set<String> getLocations(String word) {
		// TODO Auto-generated method stub
		return super.getLocations(word);
	}

	@Override
	public Set<Integer> getPositions(String word, String location) {
		// TODO Auto-generated method stub
		return super.getPositions(word, location);
	}

	@Override
	public List<SearchResult> search(Set<String> queryWords, boolean isExact) throws IOException {
		// TODO Auto-generated method stub
		return super.search(queryWords, isExact);
	}

	@Override
	public List<SearchResult> exactSearch(Set<String> queryWords) {
		// TODO Auto-generated method stub
		return super.exactSearch(queryWords);
	}

	@Override
	public List<SearchResult> partialSearch(Set<String> queries) {
		// TODO Auto-generated method stub
		return super.partialSearch(queries);
	}

	@Override
	public void writeJson(Path path) throws IOException {
		// TODO Auto-generated method stub
		super.writeJson(path);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
