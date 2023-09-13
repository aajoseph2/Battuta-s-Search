package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	public static TreeMap<Path, Integer> fileInfo = new TreeMap<>(); // TODO Reduces reusability
	/**
	 * Map of file: position
	 */
	public static TreeMap<String, List<Integer>> nestMap = new TreeMap<>();
	/**
	 * map of stem:nestMap
	 */
	public static Map<String, TreeMap<String, List<Integer>>> invertMap = new HashMap<>();
	/**
	 * map to which will be written to json, index
	 */
	public static TreeMap<String, TreeMap<String, List<Integer>>> formatMap = new TreeMap<>();

	/*
	 * TODO At least move into its own data structure class... InvertedIndex
	 * Store String, Integer instead of Path, Integer
	 */


	/**
	 * @param input the directory that recurses on its self until it reaches a base text file
	 * @throws IOException If file isunable to be read, then throw an exception
	 */
	public static void iterDirectory(Path input) throws IOException {

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
						}
					}
				}
			}
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
				ArrayList<String> stems = TextParser.listStems(line);
				for (String stem: stems) {
					processIndex(stem, input.toString(), pos);
					pos++;
				}
			}
		} catch (IOException e) {
			System.out.println("An error occurred while reading the file: " + input.toString());
		}

		String[] contents = TextParser.parse(countText.toString());

		if (contents.length != 0) {
			fileInfo.put(input, contents.length);
		}


	}

	/**
	 * @param stem stem to be added in  invertMap
	 * @param fn file name
	 * @param num position in file
	 */
	public static void processIndex(String stem, String fn, Integer num) {

		nestMap = invertMap.getOrDefault(stem, new TreeMap<>());
		List<Integer> positionsList = nestMap.getOrDefault(fn, new ArrayList<>());
		positionsList.add(num);

		nestMap.put(fn, positionsList);
		invertMap.put(stem, nestMap);


		formatMap.put(stem, nestMap);


}

	/**
	 * @return formatMap to json string
	 */
	public static String finalIndexJson() {
		StringWriter buffer = new StringWriter();

		var iterator = Driver.formatMap.entrySet().iterator();
		buffer.write("{\n");


		while (iterator.hasNext()) {
    	var entry = iterator.next();

        String stem = entry.getKey();
        String loc =  JsonFormatter.writeObjectArrays(entry.getValue());

        buffer.write("  ");
        buffer.write('"');
        buffer.write(stem);
        buffer.write("\": ");
        buffer.write(loc.toString());


        if (iterator.hasNext()) {
          buffer.write("  },\n");
      } else {
          buffer.write("  }\n");
      }
    }


    buffer.write("}");
    formatMap.clear();

    return buffer.toString();
	}


	/**
	 * TODO Describe the method here
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
			Files.write(outputPath, json.getBytes());
		} catch (IOException e) {
			System.out.println("An error occurred while reading the file: " + outputPath.toString());
		}
	}

	/**
	 * @param args Command Line Args to be read
	 */
	public static void main(String[] args)  {

		ArgumentParser map = new ArgumentParser(args);


		if (map.hasFlag("-text")) {
			Path path = map.getPath("-text");
			if (path != null) {
				try {
					invertMap.clear();
					nestMap.clear();
					fileInfo.clear();
					formatMap.clear();

					if (Files.isDirectory(path)) {
						iterDirectory(path);
					} else {
						textProcess(path);
					}
				} catch(IOException e) {
					System.out.println("Missing file path to read!\n");
				}
			}
		} else {
			System.out.println("Must input a Text file to read!");
	}

		if (map.hasFlag("-counts")) {
			if (map.getPath("-counts") == null) {
				Path countPath = Paths.get("counts.json");
				writeJsonToFile(mapToJson(), countPath);
		} else {
				Path countPath = map.getPath("-counts");
				writeJsonToFile(mapToJson(), countPath);
			}
		}

		if (map.hasFlag("-index")) {
			if (map.getPath("-index") == null) {
				Path indexPath = Paths.get("index.json");
				String indexJson = finalIndexJson();
				writeJsonToFile(indexJson, indexPath);
		} else {
				Path indexPath = map.getPath("-index");
				String indexJson = finalIndexJson();
				writeJsonToFile(indexJson, indexPath);
			}
		}
	}
}
