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
			SearchResult.query.clear();
			SearchResult.qWords.clear();
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
				JsonFormatter.writeObject(index.getWordCounts(), countPath);
			}
			catch (IOException e) {
				System.out.println("Error writing counts to file: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				Path indexPath = parser.getPath("-index", Path.of("index.json"));
				InvertedIndex.writeJson(indexPath, index);
			}
			catch (IOException e) {
				System.out.println("Error writing index to file: " + e.getMessage());
			}
		}

//		if (parser.hasFlag("-query")) {
//			Path qPath = parser.getPath("-query");
//
//			if (qPath != null) {
//				try {
//					InvertedIndexProcessor.processQuery(qPath, index);
//				}
//				catch (IOException e) {
//					System.out.println("Error writing query to file: " + e.getMessage());
//				}
//			}
//			else {
//				System.out.println("Must input query path!");
//			}
//		}
//
//		if (parser.hasFlag("-results")) {
//			Path resPath = parser.getPath("-results", Path.of("results.json"));
//			System.out.println(resPath);
//			try {
//				InvertedIndexProcessor.exactSearch(SearchResult.qWords, index);
//				SearchResult.writeQueryJson(resPath, SearchResult.query);
//			}
//			catch (IOException e) {
//				System.out.println("Error writing results to file: " + e.getMessage());
//			}
//		}
	}
}