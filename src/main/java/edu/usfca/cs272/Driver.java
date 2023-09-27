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

			ArgumentParser map = new ArgumentParser(args);
			InvertedIndex mapMethods = new InvertedIndex();

			if (map.hasFlag("-text")) {
				Path path = map.getPath("-text");
				if (path != null) {
					try {
						mapMethods.clearAll();
						if (Files.isDirectory(path)) {
							InvertedIndexProcessor.iterDirectory(path, mapMethods);
						} else {
							InvertedIndexProcessor.textProcess(path, mapMethods);
						}
					} catch(IOException e ) {
						System.out.println("Missing file path to read!\n");
					}
				}
			} else {
				System.out.println("Must input a Text file to read!");
		}

			if (map.hasFlag("-counts")) {
				try {
					Path countPath = map.getPath("-counts", Path.of("counts.json"));
					writeJsonToFile(InvertedIndexProcessor.mapToJsonCounts(mapMethods), countPath);
				} catch (IOException e) {
					System.out.println("Error writing counts to file: " + e.getMessage());
				}
			}

			if (map.hasFlag("-index")) {
				try {
				Path indexPath = map.getPath("-index", Path.of("index.json"));
					String indexJson = InvertedIndexProcessor.finalIndexJson(mapMethods);
					writeJsonToFile(indexJson, indexPath);
				} catch (IOException e) {
					System.out.println("Error writing index to file: " + e.getMessage());
				}
			}
	}
}
