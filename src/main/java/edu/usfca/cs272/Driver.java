package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
		InvertedIndex index;
		QueryProcessor queryClass = null;
		MultithreadedQueryProcessor threadedQueryClass = null;
		WorkQueue workers = null;
		WorkQueue queryWorkers = null; // TODO Try to reuse the same workers

		Function<Set<String>, List<InvertedIndex.SearchResult>> searchFunction;

		if (parser.hasFlag("-threads")) {
			index = new ThreadSafeInvertedIndex();
			workers = new WorkQueue(parser.getInteger("-threads", 5));
			queryWorkers = new WorkQueue(parser.getInteger("-threads", 5));
			searchFunction = !parser.hasFlag("-partial") ? index::exactSearch : index::partialSearch;

			threadedQueryClass = new MultithreadedQueryProcessor(searchFunction);
		}
		else {
			index = new InvertedIndex();

			searchFunction = !parser.hasFlag("-partial") ? index::exactSearch : index::partialSearch;

			queryClass = new QueryProcessor(searchFunction);
		}

		if (parser.hasFlag("-text")) {
			Path contentsPath = parser.getPath("-text");
			if (contentsPath != null) {
				try {
					if (workers != null) {
						MultiThreadProcessor.processPath(contentsPath, (ThreadSafeInvertedIndex) index, workers);
					}
					else {
						InvertedIndexProcessor.processPath(contentsPath, index);
					}
				}
				catch (IOException e) {
					System.out.println("Missing file path to read!\n");
				}
			}
			else {
				System.out.println("Must input a text file to read!");
			}
			if (workers != null) {
				workers.shutdown();
				workers.join();
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");
			if (queryPath != null) {
				try {
					if (queryWorkers != null) {
						threadedQueryClass.queryProcessor(queryPath, queryWorkers);
					}
					else {
						queryClass.queryProcessor(queryPath);
					}
				}
				catch (IOException e) {
					System.out.println("Error writing query to file: " + e.getMessage());
				}
			}
			else {
				System.out.println("Must input query path!");
			}
			if (queryWorkers != null) {
				queryWorkers.shutdown();
				queryWorkers.join();
			}
		}

		/* TODO
		if (queryWorkers != null) {
			queryWorkers.join();
		}
		*/

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
				if (queryClass == null) {
					threadedQueryClass.writeQueryJson(resPath);
				} else {
					queryClass.writeQueryJson(resPath);
				}
			}
			catch (IOException e) {
				System.out.println("Error writing results to file: " + e.getMessage());
			}
		}
	}
}
