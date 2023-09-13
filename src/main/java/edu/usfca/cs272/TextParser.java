package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

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
}
