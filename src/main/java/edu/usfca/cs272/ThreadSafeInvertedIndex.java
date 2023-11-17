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

	/**
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
		lock.writeLock().lock();
		try {
			super.addData(word, location, num);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addDistinctIndex(InvertedIndex localIndex) {
		lock.writeLock().lock();
		try {
			super.addDistinctIndex(localIndex);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean hasCount(String location) {
		lock.readLock().lock();
		try {
			return super.hasCount(location);
		}
		finally {
			lock.readLock().unlock();
		}

	}

	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasLocation(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPosition(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public SortedMap<String, Integer> getWordCounts() {
		lock.readLock().lock();
		try {
			return super.getWordCounts();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Integer numCountForFile(String location) {
		lock.readLock().lock();
		try {
			return super.numCountForFile(location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numWordFrequency(String word) {
		lock.readLock().lock();
		try {
			return super.numWordFrequency(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numTotalWordsForLocation(String location) {
		lock.readLock().lock();
		try {
			return super.numTotalWordsForLocation(location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numWordFrequencyAtLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.numWordFrequencyAtLocation(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> search(Set<String> queryWords, boolean isExact) throws IOException {
		return super.search(queryWords, isExact);
	}

	@Override
	public List<SearchResult> exactSearch(Set<String> queryWords) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queryWords);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> partialSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeJson(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.writeJson(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}
}
