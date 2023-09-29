package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Utility class for parsing, cleaning, and stemming text and text files into
 * collections of processed words.
 *
 */

public class TextParser {

	/**
	 * Text pattern to follow
	 */
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");
	/**
	 * Text pattern to follow
	 */
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");

	/**
	 * @param text text to be parsed
	 * @return cleaned text
	 */
	public static String clean(String text) {
		String cleaned = Normalizer.normalize(text, Normalizer.Form.NFD);
		cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
		return cleaned.toLowerCase();
	}

	/**
	 * @param text text to be parsed
	 * @return splitted text
	 */
	public static String[] split(String text) {
		return text.isBlank() ? new String[0] : SPLIT_REGEX.split(text.strip());
	}

	/**
	 * @param text text to be parsed
	 * @return returns cleaned and splitted text
	 */
	public static String[] parse(String text) {
		return split(clean(text));
	}

	/**
	 * Parses the line into a list of cleaned and stemmed words.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer) {
		ArrayList<String> stemList = new ArrayList<>();
		String[] words = parse(line);
		for (int i = 0; i < words.length; i++) {
			stemList.add(stemmer.stem(words[i]).toString());
		}

		return stemList;

	}

	/**
	 * Parses the line into a list of cleaned and stemmed words using the default
	 * stemmer for English.
	 *
	 * @param line the line of words to parse and stem
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see SnowballStemmer#SnowballStemmer(ALGORITHM)
	 * @see ALGORITHM#ENGLISH
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(String line) {
		Stemmer stem = new SnowballStemmer(ENGLISH);
		return listStems(line, stem);

	}

	/**
	 * Parses the line into a set of unique, sorted, cleaned, and stemmed words.
	 *
	 * @param line the line of words to parse and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		String[] words = parse(line);
		TreeSet<String> set = new TreeSet<>();
		for (int i = 0; i < words.length; i++) {
			set.add(stemmer.stem(words[i]).toString());
		}
		return set;
	}

	/**
	 * Parses the line into a set of unique, sorted, cleaned, and stemmed words
	 * using the default stemmer for English.
	 *
	 * @param line the line of words to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer#SnowballStemmer(ALGORITHM)
	 * @see ALGORITHM#ENGLISH
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		Stemmer stem = new SnowballStemmer(ENGLISH);
		return uniqueStems(line, stem);
	}

	/**
	 * Reads a file line by line, parses each line into a set of unique, sorted,
	 * cleaned, and stemmed words using the default stemmer for English.
	 *
	 * @param input the input file to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(Path input) throws IOException {

		StringBuilder inputText = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				inputText.append(line).append("\n");
			}
		}
		return uniqueStems(inputText.toString());

	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned, and
	 * stemmed words using the default stemmer for English, and adds the set of
	 * unique sorted stems to a list per line in the file.
	 *
	 * @param input the input file to parse and stem
	 * @return a list where each item is the sets of unique sorted stems parsed from
	 *   a single line of the input file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static ArrayList<TreeSet<String>> listUniqueStems(Path input) throws IOException {
		ArrayList<TreeSet<String>> listOfStems = new ArrayList<>();

		StringBuilder inputText = new StringBuilder();

		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> stemsForLine = uniqueStems(line);
				listOfStems.add(stemsForLine);
				inputText.append(line).append("\n");
			}
		}

		return listOfStems;

	}

}
