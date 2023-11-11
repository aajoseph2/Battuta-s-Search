package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * The InvertedIndexProcessor class provides functionalities for reading,
 * processing, and converting textual data into structured formats suitable for
 * building and representing an inverted index. This class contains methods to
 * recursively navigate directories, process individual text files, and
 * transform the generated inverted index data into JSON format for storage or
 * presentation.
 */

public class InvertedIndexProcessor {

	/**
	 * Offers logic to determine whether a given file input should iterate throug a
	 * direcotry structure or should processText right away.
	 *
	 * @param path Given file contents
	 * @param index map to be used for indexing data
	 * @param workers threads to do job
	 * @throws IOException if file is unreadable
	 */
	public static void processPath(Path path, InvertedIndex index, WorkQueue workers) throws IOException {

		if (Files.isDirectory(path)) {
			if (workers != null) {
				processDirectoryMultithreaded(path, index, workers);
			}
			else {
				processDirectory(path, index);
			}
		}
		else {
			processText(path, index);
		}
	}

	/*
	 * TODO Create a new class and new processPath method for multithreaded code
	 * the new processPath needs a thread-safe inverted index
	 */

	/**
	 * This recurses on its self until it reaches a base text file. Logic for the
	 * case that the file inputted is a directory.
	 *
	 * @param input the directory
	 * @param index contains the structure for the read data
	 * @throws IOException If file is unable to be read, then throw an exception.
	 */
	public static void processDirectory(Path input, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectory(entry, index);
				}
				else if (isTextFile(entry)) {
					processText(entry, index);
				}
			}
		}
	}

	/**
	 * Checks if the given path refers to a text file based on its file extension.
	 *
	 * @param input the file path to check
	 * @return true if the file is likely a text file, false otherwise
	 */
	public static boolean isTextFile(Path input) {
		String fileNameLower = input.getFileName().toString().toLowerCase();
		return fileNameLower.endsWith(".txt") || fileNameLower.endsWith(".text");
	}

	/**
	 * This is the engine of the text processing. Will take in a .txt file and input
	 * it in a Stringbuilder. This stringBuilder is then parsed into the
	 * processIndex method where the toString() will be inputted into the maps, used
	 * to read counts and index. There is also logic to convert the text into the
	 * proper stems and remove any formatting issues within the file contents, by
	 * calling helper functions within the textParser class.
	 *
	 * @param input the path of which the info will be parsed
	 * @param mapMethods mapMethods contains the structure for the read data
	 * @throws IOException IOException In case file cannot be read
	 */
	public static void processText(Path input, InvertedIndex mapMethods) throws IOException {
		int pos = 1;
		String location = input.toString();
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			while ((line = reader.readLine()) != null) {
				String[] words = TextParser.parse(line);
				for (String word : words) {
					mapMethods.addData(stemmer.stem(word).toString(), location, pos);
					pos++;
				}
			}
		}
	}

	/**
	 * Multi threaded version of processDirectory
	 *
	 * @param input the directory
	 * @param index contains the structure for the read data
	 * @param worker workers threads to do job
	 * @throws IOException If file is unable to be read, then throw an exception.
	 */
	public static void processDirectoryMultithreaded(Path input, InvertedIndex index, WorkQueue worker) // TODO thread-safe index
			throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
							processDirectoryMultithreaded(entry, index, worker);
				}
				else if (isTextFile(entry)) {
					worker.execute(() -> {
						try {
							InvertedIndex localIndex = new InvertedIndex();
							processText(entry, localIndex);
							index.addAll(localIndex);
						}
						catch (IOException e)  {
							throw new UncheckedIOException(e);
						}
					});
				}
			}
		}
	}

}
