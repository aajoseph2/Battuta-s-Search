package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
	 * This method simply takes in the Strings built from mapToJsonCounts() and
	 * finalIndexJson(), writes it into the desired outputPath with the pretty json
	 * format.
	 *
	 * @param json Srting to be parsed in the outputPath
	 * @param outputPath the final destination of the parsed info
	 * @throws IOException In case there is any issue with write file
	 */
	public static void writeJsonToFile(String json, Path outputPath) throws IOException {
		Files.write(outputPath, json.getBytes());
	}

	/***
	 * Main method to process command-line arguments and manage the inverted index.
	 * Read and process text files or directories provided using the "-text" flag.
	 * Write term counts to a file using the "-counts" flag. Write the entire index
	 * to a file using the "-index" flag.
	 *
	 * @param args Command Line Args to be read
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = new InvertedIndex();

		if (parser.hasFlag("-text")) {
			Path path = parser.getPath("-text");
			if (path != null) {
				try {
					boolean flag = Files.isDirectory(path);
					InvertedIndexProcessor.processPath(path, index, flag);
				}
				catch (IOException e) {
					System.out.println("Missing file path to read!\n");
				}
			}
		}
		else {
			System.out.println("Must input a Text file to read!");
		}

		if (parser.hasFlag("-counts")) {
			try {
				Path countPath = parser.getPath("-counts", Path.of("counts.json"));
				// TODO JsonFormatter.writeObject(index.getWordCounts());
				writeJsonToFile(InvertedIndexProcessor.mapToJsonCounts(index), countPath);
			}
			catch (IOException e) {
				System.out.println("Error writing counts to file: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				Path indexPath = parser.getPath("-index", Path.of("index.json"));
				writeJsonToFile( JsonFormatter.writeIndexJson(index), indexPath);
			}
			catch (IOException e) {
				System.out.println("Error writing index to file: " + e.getMessage());
			}
		}
	}
}
