package edu.usfca.cs272;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchHistory {

	private final List<String> searchHistory;

	public SearchHistory() throws IOException {
		searchHistory = new ArrayList<>();
	}

	public void addSearchedQuery(String word) {
		searchHistory.add(word);
	}

	public List<String> getSearchedList() {
		return Collections.unmodifiableList(searchHistory);
	}

	public void clearSearchHistory() {
		searchHistory.clear();
	}

}