package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Processes queries that are used to as the worsd to be searched for from the
 * given indexed structures
 */
public class ProcessQuery {

	/**
	 * Structure of word query as the key, and the SearchResult as the value
	 */
	public Map<String, List<SearchResult>> query = new TreeMap<>();

	/**
	 * @param location Where the query is being retrieved from
	 * @param mapMethods mapMethods contains the structure for the read data
	 * @param isExact boolean to see if exact or partial
	 * @param query structure containing searched data
	 * @throws IOException If file is unreadable
	 */
	public static void processQuery(Path location, InvertedIndex mapMethods, boolean isExact, ProcessQuery query)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(location, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				processQuery(line, mapMethods, isExact, query);
			}
		}
	}

	/**
	 * Helper to process query line by line
	 *
	 * @param line the specific line of words intended to query
	 * @param mapMethods contains the structure for the read data
	 * @param isExact boolean to see if exact or partial
	 * @param query structure containing searched data
	 * @throws IOException If file is unreadable
	 */
	public static void processQuery(String line, InvertedIndex mapMethods, boolean isExact, ProcessQuery query)
			throws IOException {
		String[] words = TextParser.parse(line);
		var buffer = TextParser.uniqueStems(Arrays.toString(words));
		if (!buffer.isEmpty()) {
			List<SearchResult> currentResults = mapMethods.search(buffer, isExact);
			query.query.put(String.join(" ", buffer), currentResults);

		}
	}

}
