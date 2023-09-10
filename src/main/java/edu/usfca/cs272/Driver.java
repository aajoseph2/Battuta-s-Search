package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

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

		StringBuilder inputText = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			int pos = 0;
			while ((line = reader.readLine()) != null) {
				inputText.append(line).append("\n");

				String[] buffer = parse(line.toString());

				for (String text: buffer) {
					processIndex(text, input.toString(), pos);
				}

				pos++;
			}
		} catch (IOException e) {
			System.out.println("An error occurred while reading the file: " + input.toString());
		}

		String[] contents = parse(inputText.toString());

/*		for (String text: contents) {
			processIndex(text, input.toString());
		}*/

		if (contents.length != 0) {
			fileInfo.put(input, contents.length);
		}

	}

	public static void processIndex (String stem, String fn, Integer num) {

		if (invertMap.containsKey(stem)) {
			if (nestMap.get(fn) == null) {
				 List<Integer> temp = new ArrayList<Integer>();
				 temp.add(num);
				nestMap.put(fn, temp);
			} else {
				var tempTwo = nestMap.get(fn);
				tempTwo.add(num);
				invertMap.put(stem, nestMap);
			}
		} else {
			List<Integer> tempThree = new ArrayList<Integer>();
			tempThree.add(num);
			nestMap.put(fn, tempThree);
			invertMap.put(stem, nestMap);
		}

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
		HashMap<String, String> flags = new HashMap<>();
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

			// case "-index" :
			// System.out.println("Pending");

			default:
				System.out.println("Ignoring unknown argument: " + flag);
				break;
			}
		}

		System.out.println(invertMap);

	}

}
