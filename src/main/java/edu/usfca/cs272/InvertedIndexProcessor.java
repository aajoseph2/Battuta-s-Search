package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * The InvertedIndexProcessor class provides functionalities for reading,
 * processing, and converting textual data into structured formats suitable for
 * building and representing an inverted index. This class contains methods to
 * recursively navigate directories, process individual text files, and
 * transform the generated inverted index data into JSON format for storage or
 * presentation.
 */

public class InvertedIndexProcessor {

	/**
	 * Offers logic to determine whether a given file input should iterate throug a
	 * direcotry structure or should processText right away.
	 *
	 * @param path Given file contents
	 * @param index map to be used for indexing data
	 * @param flag if original directory is a directory or text file
	 * @throws IOException if file is unreadable
	 */
	public static void processPath(Path path, InvertedIndex index, boolean flag) throws IOException {

		if (Files.isDirectory(path)) {
			processDirectory(path, index, flag);
		}
		else if (isTextFile(path) && flag) {
			processText(path, index);
		}
		else if (!flag) {
			processText(path, index);
		}
	}

	/**
	 * This recurses on its self until it reaches a base text file. Logic for the
	 * case that the file inputted is a directory.
	 *
	 * @param input the directory
	 * @param index contains the structure for the read data
	 * @param flag if original directory is a directory or text file
	 * @throws IOException If file is unable to be read, then throw an exception.
	 */
	public static void processDirectory(Path input, InvertedIndex index, boolean flag) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				processPath(entry, index, flag);
			}
		}
	}

	/**
	 * Checks if the given path refers to a text file based on its file extension.
	 *
	 * @param input the file path to check
	 * @return true if the file is likely a text file, false otherwise
	 */
	public static boolean isTextFile(Path input) {
		String fileNameLower = input.getFileName().toString().toLowerCase();
		return fileNameLower.endsWith(".txt") || fileNameLower.endsWith(".text");

	}

	/**
	 * This is the engine of the text processing. Will take in a .txt file and input
	 * it in a Stringbuilder. This stringBuilder is then parsed into the
	 * processIndex method where the toString() will be inputted into the maps, used
	 * to read counts and index. There is also logic to convert the text into the
	 * proper stems and remove any formatting issues within the file contents, by
	 * calling helper functions within the textParser class.
	 *
	 * @param input the path of which the info will be parsed
	 * @param mapMethods mapMethods contains the structure for the read data
	 * @throws IOException IOException In case file cannot be read
	 */
	public static void processText(Path input, InvertedIndex mapMethods) throws IOException {

		int pos = 1;
		String location = input.toString();

		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			while ((line = reader.readLine()) != null) {
				String[] words = TextParser.parse(line);

				for (String word : words) {
					mapMethods.addData(stemmer.stem(word).toString(), location, pos);
					pos++;
				}
			}
		}

		if (pos > 1) {
			mapMethods.addWordCount(location, pos - 1);
		}
	}

	/**
	 * @param location Where the query is being retrieved from
	 * @param mapMethods mapMethods contains the structure for the read data
	 * @return List that contians the sets of queries to be read
	 * @throws IOException If file is unreadable
	 */
	public static void processQuery(Path location, InvertedIndex mapMethods) throws IOException {

		// List<TreeSet<String>> qList = new ArrayList<TreeSet<String>>();

		try (BufferedReader reader = Files.newBufferedReader(location, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = TextParser.parse(line);
				var buffer = TextParser.uniqueStems(Arrays.toString(words));
				if (!buffer.isEmpty()) {
					SearchResult.qWords.add(TextParser.uniqueStems(Arrays.toString(words)));
				}
			}
		}
		// return qWords;
	}

	/**
	 * @param query Words to be searched in the inverted index
	 * @param mapMethods mapMethods contains the structure for the read data
	 * @return list of the query
	 * @throws IOException
	 */
	public static void exactSearch(List<TreeSet<String>> query, InvertedIndex mapMethods) throws IOException {

		// Map<String, List<SearchResult>> SearchResult.query = new TreeMap<>();

		for (TreeSet<String> entry : query) {
			Map<String, Integer> locationCounts = new HashMap<>();
			int totalWords = 0;

			for (String word : entry) {
				Set<String> locations = mapMethods.getLocations(word);
				for (String loc : locations) {
					int wordCountAtLocation = mapMethods.getWordFrequencyAtLocation(word, loc);
					locationCounts.put(loc, locationCounts.getOrDefault(loc, 0) + wordCountAtLocation);
				}
			}

			List<SearchResult> currentResults = new ArrayList<>();
			for (var locEntry : locationCounts.entrySet()) {
				totalWords = mapMethods.getTotalWordsForLocation(locEntry.getKey());
				double score = (double) locEntry.getValue() / totalWords;
				currentResults.add(new SearchResult(locEntry.getKey(), locEntry.getValue(), score));
			}
			Collections.sort(currentResults);

			SearchResult.query.put(String.join(" ", entry), currentResults);
		}

		// System.out.println(JsonFormatter.writeSearchResults(SearchResult.query));

		// return SearchResult.query;
	}

}
