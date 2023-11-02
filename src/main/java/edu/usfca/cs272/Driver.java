package edu.usfca.cs272;

import java.io.IOException;
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
		boolean isExact = !parser.hasFlag("-partial");
		QueryProcessor queryClass = new QueryProcessor(index, isExact);

		if (parser.hasFlag("-text")) {
			Path contentsPath = parser.getPath("-text");
			if (contentsPath != null) {
				try {
					InvertedIndexProcessor.processPath(contentsPath, index);
				}
				catch (IOException e) {
					System.out.println("Missing file path to read!\n");
				}
			}
			else {
				System.out.println("Must input a text file to read!");
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");
			if (queryPath != null) {
				try {
					queryClass.queryProcessor(queryPath);
				}
				catch (IOException e) {
					System.out.println("Error writing query to file: " + e.getMessage());
				}
			}
			else {
				System.out.println("Must input query path!");
			}
		}

		if (parser.hasFlag("-counts")) {
			try {
				Path countPath = parser.getPath("-counts", Path.of("counts.json"));
				JsonFormatter.writeObject(index.getWordCounts(), countPath);
			}
			catch (IOException e) {
				System.out.println("Error writing counts to file: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				Path indexPath = parser.getPath("-index", Path.of("index.json"));
				index.writeJson(indexPath);
			}
			catch (IOException e) {
				System.out.println("Error writing index to file: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-results")) {
			Path resPath = parser.getPath("-results", Path.of("results.json"));
			try {
				queryClass.writeQueryJson(resPath);
			}
			catch (IOException e) {
				System.out.println("Error writing results to file: " + e.getMessage());
			}
		}

	}
}
