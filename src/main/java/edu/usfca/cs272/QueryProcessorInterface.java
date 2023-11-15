package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Interface for the query proccesor classes
 */
public interface QueryProcessorInterface {
	void queryProcessor(Path location) throws IOException;

  void queryProcessor(String line) throws IOException;

  List<InvertedIndex.SearchResult> getQueryResults(String queryLine);

  Set<String> getQueryLines();

  boolean hasQuery(String queryLine);

  int queryCount();

  void writeQueryJson(Path path) throws IOException;

  @Override
  String toString();
}
