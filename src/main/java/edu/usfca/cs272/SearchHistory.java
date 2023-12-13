package edu.usfca.cs272;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class manages the search history, storing and providing access to a list
 * of previously searched queries.
 */
public class SearchHistory {

	/**
	 * An instance that tracks and stores the history of search queries. Provides
	 * functionality to add, retrieve, and clear the history of searches made by
	 * users. Ensures that the search history is maintained accurately across
	 * different servlet requests.
	 */
	private final List<String> searchHistory;

	/**
	 * Constructs a new SearchHistory object with an empty list to store search
	 * queries.
	 *
	 * @throws IOException If there is issue reading query from source
	 */
	public SearchHistory() throws IOException {
		searchHistory = new ArrayList<>();
	}

	/**
	 * Adds a searched query to the search history.
	 *
	 * @param word The search query to be added to the history.
	 */
	public void addSearchedQuery(String word) {
		searchHistory.add(word);
	}

	/**
	 * Retrieves an unmodifiable list of searched queries.
	 *
	 * @return An unmodifiable list representing the search history.
	 */
	public List<String> getSearchedList() {
		return Collections.unmodifiableList(searchHistory);
	}

	/**
	 * Clears all entries from the search history.
	 */
	public void clearSearchHistory() {
		searchHistory.clear();
	}

}
