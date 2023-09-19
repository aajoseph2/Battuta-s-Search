package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
	 * This imports methods from GeneralInfo.java. These methods use wrapper calls
	 * to alter maps that are used to build the counts or indeces of the file contents.
	 * The data structure is located in this seperate class, where data can be stored
	 * and called.
	 */
	public static GeneralFileInfo mapMethods = new GeneralFileInfo();

	// TODO Move into a InvertedIndexProcessor (anything file IO  reading stuff goes here...)
	/**
	 * This recurses on its self until it reaches a base text file. Logic for the case
	 * that the file inputted is a directory.
	 * @param input the directory
	 * @throws IOException If file is unable to be read, then throw an exception.
	 */
	public static void iterDirectory(Path input) throws IOException {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				if (entry.getFileName().toString().equals(".DS_Store")) { // TODO What does this break?
					continue;
				}
				if (Files.isDirectory(entry)) {
					iterDirectory(entry);
				} else {
					String fileNameLower = entry.getFileName().toString().toLowerCase();
					if (fileNameLower.endsWith(".txt") || fileNameLower.endsWith(".text")) {
							textProcess(entry);
					}
				}
			}
		}
	}

	/**
	 * This is the engine of the text processing. Will take in a .txt file
	 * and input it in  a Stringbuilder. This stringBuilder is then parsed into
	 * the processIndex method where the toString() will be inputted into the
	 * maps, used to read counts and index. There is also logic to convert the
	 * text into the proper stems and remove any formatting issues within the
	 * file contents, by calling helper functions within the textParser class.
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
		}

		String[] contents = TextParser.parse(countText.toString());

		if (contents.length != 0) {
			mapMethods.addFileCountsInfo(input, contents.length);
		}


	}

	/**
	 * This method processes data from the textProcess methods, where the data will
	 * be inputted into maps, which are used to build a final data structure to read
	 * the counts and index of the file contents.
	 * @param stem stem to be added in  invertMap
	 * @param fn file name, to be added within nestMap
	 * @param num position in file
	 */
	public static void processIndex(String stem, String fn, Integer num) { // TODO Make this an add method in the data structure?

		TreeMap<String, List<Integer>> nestMap = mapMethods.getFormatVal(stem);
		List<Integer> positionsList = nestMap.getOrDefault(fn, new ArrayList<>());
		positionsList.add(num);

		nestMap.put(fn, positionsList);
		mapMethods.addFormatInfo(stem, nestMap);
}

	/**
	 * Converts the formatted map, into json pretty text that follows the standard
	 * of the example from the readMe.
	 * @return stringBuilder.toString() that is ready to be inputted within writeJsonToFile()
	 */
	public static String finalIndexJson() {
		StringWriter buffer = new StringWriter();
		TreeMap<String, TreeMap<String, List<Integer>>> formatMap = mapMethods.getFormatMap();

		var iterator = formatMap.entrySet().iterator();
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

		return buffer.toString();
	}


	/**
	 * This method takes in the map of fileCountsInfo, and converts the contents into
	 * a string of pretty json. This string is meant for the "-counts" flag and will
	 * be inputted within writeToJsonFile eventually for the output.
	 * @return converted pretty json string taken from a map
	 */
	public static String mapToJsonCounts() {
		StringBuilder json = new StringBuilder("{\n");
		TreeMap<Path, Integer> fileCountsInfo =  mapMethods.getFileCountsInfo();

		for (var entry : fileCountsInfo.entrySet()) {
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
	 * This method simply takes in the Strings built from mapToJsonCounts() and
	 * finalIndexJson(), writes it into the desired outputPath with the pretty
	 * json format.
	 * @param json Srting to be parsed in the outputPath
	 * @param outputPath the final destination of the parsed info
	 * @throws IOException In case there is any issue with write file
	 */
	public static void writeJsonToFile(String json, Path outputPath) throws IOException{
			Files.write(outputPath, json.getBytes());
	}

	/**
	 * @param args Command Line Args to be read
	 */
	public static void main(String[] args)  {

		try {
			ArgumentParser map = new ArgumentParser(args);
			// TODO GeneralFileInfo mapMethods = new GeneralFileInfo();

			if (map.hasFlag("-text")) {
				Path path = map.getPath("-text");
				if (path != null) {
					try {
						mapMethods.clearAll();
						if (Files.isDirectory(path)) {
							iterDirectory(path);
						} else {
							textProcess(path);
						}
					} catch(IOException e ) {
						System.out.println("Missing file path to read!\n");
					}
				}
			} else {
				System.out.println("Must input a Text file to read!");
		}

			if (map.hasFlag("-counts")) {
				// TODO Smaller try catch inside of here
				if (map.getPath("-counts") == null) {
					Path countPath = Paths.get("counts.json");
					writeJsonToFile(mapToJsonCounts(), countPath);
			} else {
					Path countPath = map.getPath("-counts");
					writeJsonToFile(mapToJsonCounts(), countPath);
				}
			}

			if (map.hasFlag("-index")) {
				// TODO Path indexPath = map.getPath("-index", Path.of("index.json"));
				
				// TODO Smaller try catch inside here
				if (map.getPath("-index") == null) {
					Path indexPath = Paths.get("index.json"); // TODO Path.of
					String indexJson = finalIndexJson();
					writeJsonToFile(indexJson, indexPath);
			} else {
					Path indexPath = map.getPath("-index");
					String indexJson = finalIndexJson();
					writeJsonToFile(indexJson, indexPath);
				}
			}
	} catch (IOException e) {
		System.out.println("An error occurred: " + e.getMessage());
		}
	}
}
