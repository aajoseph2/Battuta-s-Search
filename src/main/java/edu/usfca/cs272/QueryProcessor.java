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
public class QueryProcessor {

	/*
	 * TODO You know how to think about classes with instance data like the query
	 * map now! Practice those design principles and (1) encapsulate this data and
	 * (2) create safe useful methods for accessing that data.
	 */

	/**
	 * Structure of word query as the key, and the SearchResult as the value
	 */
	public Map<String, List<InvertedIndex.SearchResult>> query = new TreeMap<>();

	/*
	 * TODO Take a non-static approach in this class. Unlike your inverted index
	 * processor class, this one needs to store extra information and doesn't work
	 * as well as a class with only static methods.
	 */

	/*
	 * TODO Consider the query line "hello world". When does that query line
	 * generate the same results (and thus should be stored in the same query map)?
	 * If we have two different inverted index instances (one built from text files,
	 * another built from web pages), do they return the same list of results? How
	 * about doing an exact versus partial search for that query line?
	 *
	 * If it does not result in the same results, it should not be stored in the
	 * same query map. In that cause, instead of making those values parameters of a
	 * method as below, it should be parameters sent to the constructor of this
	 * class and stored as final members.
	 */

	/**
	 * @param location Where the query is being retrieved from
	 * @param mapMethods mapMethods contains the structure for the read data
	 * @param isExact boolean to see if exact or partial
	 * @param query structure containing searched data
	 * @throws IOException If file is unreadable
	 */
	public static void queryProcessor(Path location, InvertedIndex mapMethods, boolean isExact, QueryProcessor query)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(location, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				queryProcessor(line, mapMethods, isExact, query);
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
	public static void queryProcessor(String line, InvertedIndex mapMethods, boolean isExact, QueryProcessor query)
			throws IOException {
		String[] words = line.split(" ");
		var buffer = TextParser.uniqueStems(Arrays.toString(words));
		if (!buffer.isEmpty()) {
			List<InvertedIndex.SearchResult> currentResults = mapMethods.search(buffer, isExact);
			query.query.put(String.join(" ", buffer), currentResults);

			/*
			 * TODO Check out the input/query/respect.txt query file and compare that file
			 * to the search results in expected/exact/exact-respect-stems.json file. Notice
			 * how many lines are in the query file, versus how many unique lines show up in
			 * the results. How many times does yout code need to calculate those search
			 * results? How many times does your code re-calculate those results when it
			 * doesn't need to?
			 */
		}
	}

}
