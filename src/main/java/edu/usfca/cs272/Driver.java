package edu.usfca.cs272;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. Driver starts server.
 *
 * @author Amin Joseph
 * @version Fall 2023
 */
public class Driver {

	/***
	 * Main method to process command-line arguments and manage the inverted index.
	 * Read and process text files or directories provided using a series of
	 * different flags that enable multithreading, crawl through a website, query
	 * results, crawl through a local directory, and output results into specified
	 * json files.
	 *
	 * @param args Command Line Args to be read
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index;
		ThreadSafeInvertedIndex safe = null;
		QueryProcessorInterface queryProcessor;
		WorkQueue workers = null;

		if (parser.hasFlag("-threads") || parser.hasFlag("-html") || parser.hasFlag("-server")) {
			safe = new ThreadSafeInvertedIndex();
			index = safe;
			workers = new WorkQueue(parser.getInteger("-threads", 5));
			queryProcessor = new MultithreadedQueryProcessor(workers, !parser.hasFlag("-partial"), safe);
		}
		else {
			index = new InvertedIndex();
			queryProcessor = new QueryProcessor(!parser.hasFlag("-partial"), index);
		}

		if (parser.hasFlag("-text")) {
			Path contentsPath = parser.getPath("-text");
			if (contentsPath != null) {
				try {
					if (workers != null) {
						MultiThreadProcessor.processPath(contentsPath, safe, workers);
					}
					else {
						InvertedIndexProcessor.processPath(contentsPath, index);
					}
				}
				catch (IOException e) {
					System.out.println("File path either missing or incorrect path!\n");
				}
			}
			else {
				System.out.println("Must input a text file to read!");
			}
		}

		if (parser.hasFlag("-html")) {
			String seed = parser.getString("-html");
			if (seed != null && !seed.isBlank()) {
				try {
					Crawler crawler = new Crawler(safe, parser.getInteger("-crawl", 1), workers);
					crawler.startCrawl(LinkFinder.convertUrl(new URL(seed), seed));
				}
				catch (Exception e) {
					System.out.println("Error processing HTML from seed: " + e.getMessage());
				}
			}
			else {
				System.out.println("A seed URL must be provided with the -html flag.");
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");
			if (queryPath != null) {
				try {
					queryProcessor.queryProcessor(queryPath);
				}
				catch (IOException e) {
					System.out.println("Error writing query to file: " + e.getMessage());
				}
			}
			else {
				System.out.println("Must input query path!");
			}
		}

		if (workers != null) {
			workers.shutdown();
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
				queryProcessor.writeQueryJson(resPath);
			}
			catch (IOException e) {
				System.out.println("Error writing results to file: " + e.getMessage());
			}
		}
		if (parser.hasFlag("-server")) {
			try {
				SearchHistory searchHistory = new SearchHistory();
				SearchEngine engine = new SearchEngine(safe, searchHistory);
				engine.runServer(parser.getInteger("-server", 8080));
			}
			catch (Exception e) {
				System.out.println("Unexpected error encountered while starting the server: " + e.getMessage());
			}
		}
	}
}
