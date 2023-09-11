package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Amin Joseph
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 */

	public static TreeMap<Path, Integer> fileInfo = new TreeMap<>();
	public static Map<String, List<Integer>> nestMap = new HashMap<>();
	public static Map<String, Map<String, List<Integer>>> invertMap = new HashMap<>();
	public static Map<String, String> formatMap = new HashMap<>();
	/**
	 * Text pattern to follow
	 */
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");
	/**
	 * Text pattern to follow
	 */
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");


	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {

		writer.write("[\n");

	    Iterator<? extends Number> iterator = elements.iterator();
	    while (iterator.hasNext()) {
	        Number element = iterator.next();

	        writeIndent(writer, indent + 1);
	        writer.write(element.toString());

	        if (iterator.hasNext()) {
	            writer.write(",");
	        }
	        writer.write("\n");
	    }

	    writeIndent(writer, indent);
	    writer.write("]");


	}



	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @return String
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer, int indent) throws IOException {
	    writer.write("{\n");

	    var iterator = elements.entrySet().iterator();

	    while (iterator.hasNext()) {
	        Map.Entry<String, ? extends Collection<? extends Number>> entry = iterator.next();

	        String elementString = entry.getKey();
	        Collection<? extends Number> elementCollection = entry.getValue();

	        writeQuote(elementString, writer, indent + 1);
	        writer.write(": ");

	        if (elementCollection != null && !elementCollection.isEmpty()) {
	            writeArray(elementCollection, writer, indent + 1);
	        } else {
	            writer.write("[\n");
	            writeIndent(writer, indent + 1);
	            writer.write("]");
	        }

	        if (iterator.hasNext()) {
	            writer.write(",");
	        }
	        writer.write("\n");
	    }

	    writeIndent(writer, indent);
	    writer.write("}");

	    return writer.toString();
	}


	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

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
	 * @see #addStems(String, Stemmer, Collection)
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
	 * @param input the directory that recurses on its self until it reaches a base text file
	 */
	public static void iterDirectory(Path input) {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				if (entry.getFileName().toString().equals(".DS_Store")) {
					continue;
				}
				if (Files.isDirectory(entry)) {
					iterDirectory(entry);
				} else {
					String fileNameLower = entry.getFileName().toString().toLowerCase();
					if (fileNameLower.endsWith(".txt") || fileNameLower.endsWith(".text")) {
						try {
							textProcess(entry);
						} catch (MalformedInputException e) {
							System.out.println("Skipped due to encoding issues: " + entry);
						} catch (IOException e) {
							System.out.println("An error occurred while iterating the directory: " + input.toString());
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("File was not able to be read!");

		}
	}

	/**
	 * @param input the path of which the info will be parsed
	 * @throws IOException IOException In case file cannot be read
	 */
	public static void textProcess(Path input) throws IOException {

		if (!Files.exists(input)) {
			System.out.println("Invalid file: " + input.toString());
			return;
		}

		StringBuilder countText = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			int pos = 1;
			while ((line = reader.readLine()) != null) {

				countText.append(line).append("\n");
				ArrayList<String> stems = listStems(line);

				for (String stem: stems) {
					processIndex(stem, input.toString(), pos);
				}
				pos++;
			}
		} catch (IOException e) {
			System.out.println("An error occurred while reading the file: " + input.toString());
		}

		String[] contents = parse(countText.toString());

		if (contents.length != 0) {
			fileInfo.put(input, contents.length);
		}


	}

	public static void processIndex(String stem, String fn, Integer num) {

		nestMap = invertMap.getOrDefault(stem, new HashMap<>());
		List<Integer> positionsList = nestMap.getOrDefault(fn, new ArrayList<>());
		positionsList.add(num);

		nestMap.put(fn, positionsList);
		invertMap.put(stem, nestMap);

		writeObjectArrays(nestMap);
		formatMap.put(stem, writeObjectArrays(nestMap));

}

	public static void finalIndexJson() {
		StringWriter buffer = new StringWriter();


		buffer.write("{\n");


    for (var entry: Driver.formatMap.entrySet()) {

        String stem = entry.getKey();
        String loc = entry.getValue();

        buffer.write('"');
        buffer.write(stem);
        buffer.write("\": ");
        buffer.write(loc.toString());
    }


    buffer.write("}");

    System.out.println(buffer);
	}


	/**
	 * @return converted json string taken from a map
	 */
	public static String mapToJson() {
		StringBuilder json = new StringBuilder("{\n");

		for (var entry : fileInfo.entrySet()) {
			json.append("  \"").append(entry.getKey()).append("\": ").append(entry.getValue()).append(",\n");
		}

		if (json.length() > 2) {
			json.setLength(json.length() - 2);
		} else {
			return "{\n}";
		}

		json.append("\n}");
		return json.toString();
	}

	/**
	 * @param json Srting to be parsed in the outputPath
	 * @param outputPath the final destination of the parsed info
	 */
	public static void writeJsonToFile(String json, Path outputPath) {
		try {
			System.out.println(outputPath);
			Files.write(outputPath, json.getBytes());
		} catch (IOException e) {
			System.out.println("An error occurred while reading the file: " + outputPath.toString());
		}
	}

	/**
	 * @param args Command Line Args to be read
	 * @throws IOException In case file cannot be read
	 */
	public static void main(String[] args) throws IOException {
		LinkedHashMap<String, String> flags = new LinkedHashMap<>();
		int bound = args.length;

		for (int i = 0; i < bound; i++) {
			if (args[i].startsWith("-")) {
				if ((i + 1 < bound) && (!args[i + 1].startsWith("-"))) {
					flags.put(args[i], args[i + 1]);
					i++;
				} else {
					flags.put(args[i], "default");
				}

			}
		}

		for (var commands : flags.entrySet()) {
			String flag = commands.getKey();
			String path = commands.getValue();


			switch (flag) {

			case "-text":
				fileInfo.clear();
				if (path.equals("default")) {
					System.out.println("Missing file path to read!\n");
					continue;
			} else {
					Path content = Paths.get(path);
					if (Files.isDirectory(content)) {
						iterDirectory(content);
			} else {
						textProcess(content);
					}
				}
				break;

			case "-counts":
				if (path.equals("default")) {
					Path countPath = Paths.get("counts.json");
					writeJsonToFile(mapToJson(), countPath);
			} else {
					Path countPath = Paths.get(path);
					writeJsonToFile(mapToJson(), countPath);
				}
				break;

			 case "-index" :
				 if (path.equals("default")) {
						Path indexPath = Paths.get("index.json");
						finalIndexJson();
				} else {
						Path indexPath = Paths.get(path);
					}
					break;

			default:
				System.out.println("Ignoring unknown argument: " + flag);
				break;
			}
		}

	}

}
