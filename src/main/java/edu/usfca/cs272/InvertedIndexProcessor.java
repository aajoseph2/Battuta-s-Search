package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * The InvertedIndexProcessor class provides functionalities for reading,
 * processing, and converting textual data into structured formats suitable for
 * building and representing an inverted index. This class contains methods to
 * recursively navigate directories, process individual text files, and
 * transform the generated inverted index data into JSON format for storage or
 * presentation.
 */

public class InvertedIndexProcessor {
	
	/* TODO Call this in Driver instead
	public static void processPath(Path path, InvertedIndex index) throws IOException {
		if (Files.isDirectory(path)) {
			iterDirectory(path, index);
		}
		else {
			processText(path, index);
		}
	}
	*/
	
	/**
	 * This recurses on its self until it reaches a base text file. Logic for the
	 * case that the file inputted is a directory.
	 *
	 * @param input the directory
	 * @param mapMethods contains the structure for the read data
	 * @throws IOException If file is unable to be read, then throw an exception.
	 */
	public static void iterDirectory(Path input, InvertedIndex mapMethods) throws IOException { // TODO processDirectory
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					iterDirectory(entry, mapMethods);
				}
				else {
					if (isTextFile(entry)) {
						processText(entry, mapMethods);
					}
				}
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
		// TODO return (fileNameLower.endsWith(".txt") || fileNameLower.endsWith(".text");
		if (fileNameLower.endsWith(".txt") || fileNameLower.endsWith(".text")) {
			return true;
		}
		return false;
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
		// TODO String location = input.toString(); and reuse below
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			// TODO Stemmer stemmer = ...
			while ((line = reader.readLine()) != null) {
				// TODO Call parse directly here... loop and stem, add directly to the index (never to a list)
				ArrayList<String> stems = TextParser.listStems(line);
				for (String stem : stems) {
					mapMethods.addData(stem, input.toString(), pos);
					pos++;
				}
			}
		}

		if (pos > 1) {
			mapMethods.addWordCount(input.toString(), pos - 1);
		}
	}

	/**
	 * This method takes in the map of fileCountsInfo, and converts the contents
	 * into a string of pretty json. This string is meant for the "-counts" flag and
	 * will be inputted within writeToJsonFile eventually for the output.
	 *
	 * @param mapMethods contains the structure for the read data
	 * @return converted pretty json string taken from a map
	 */
	public static String mapToJsonCounts(InvertedIndex mapMethods) { // TODO Remove
		StringBuilder json = new StringBuilder("{\n");
		TreeMap<String, Integer> fileCountsInfo = mapMethods.getWordCounts();

		for (var entry : fileCountsInfo.entrySet()) {
			json.append("  \"").append(entry.getKey()).append("\": ").append(entry.getValue()).append(",\n");
		}

		if (json.length() > 2) {
			json.setLength(json.length() - 2);
		}
		else {
			return "{\n}";
		}

		json.append("\n}");
		return json.toString();
	}
}