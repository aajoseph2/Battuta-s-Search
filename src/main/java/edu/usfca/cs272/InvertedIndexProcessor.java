package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * This recurses on its self until it reaches a base text file. Logic for the
	 * case that the file inputted is a directory.
	 *
	 * @param input the directory
	 * @param mapMethods contains the structure for the read data
	 * @throws IOException If file is unable to be read, then throw an exception.
	 */
	public static void iterDirectory(Path input, InvertedIndex mapMethods) throws IOException {
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
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
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
	 * Converts the formatted map, into json pretty text that follows the standard
	 * of the example from the readMe.
	 *
	 * @param mapMethods contains the structure for the read data
	 * @return stringBuilder.toString() that is ready to be inputted within
	 *   writeJsonToFile()
	 */
	public static String finalIndexJson(InvertedIndex mapMethods) { // TODO Move to the JsonWriter
		StringWriter buffer = new StringWriter();
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> formatMap = mapMethods.getInvertedIndex();

		var iterator = formatMap.entrySet().iterator();
		buffer.write("{\n");

		while (iterator.hasNext()) {
			var entry = iterator.next();

			String stem = entry.getKey();
			String loc = JsonFormatter.writeObjectArrays(entry.getValue());

			buffer.write("  ");
			buffer.write('"');
			buffer.write(stem);
			buffer.write("\": ");
			buffer.write(loc.toString());

			if (iterator.hasNext()) {
				buffer.write("  },\n");
			}
			else {
				buffer.write("  }\n");
			}
		}

		buffer.write("}");

		return buffer.toString();
	}

	/**
	 * This method takes in the map of fileCountsInfo, and converts the contents
	 * into a string of pretty json. This string is meant for the "-counts" flag and
	 * will be inputted within writeToJsonFile eventually for the output.
	 *
	 * @param mapMethods contains the structure for the read data
	 * @return converted pretty json string taken from a map
	 */
	public static String mapToJsonCounts(InvertedIndex mapMethods) {
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
