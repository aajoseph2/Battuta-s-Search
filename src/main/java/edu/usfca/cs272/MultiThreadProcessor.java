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
 * Multithreaded class of InvertedIndexProcessor class
 */
public class MultiThreadProcessor {

	/**
	 * Offers logic to determine whether a given file input should iterate throug a
	 * direcotry structure or should processText right away.
	 *
	 * @param path Given file contents
	 * @param index map to be used for indexing data
	 * @param workers threads to do job
	 * @throws IOException if file is unreadable
	 */
	public static void processPath(Path path, ThreadSafeInvertedIndex index, WorkQueue workers) throws IOException {

		if (Files.isDirectory(path)) {
			processDirectoryMultithreaded(path, index, workers);
		}
		else {
			processText(path, index);
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
	public static void processText(Path input, ThreadSafeInvertedIndex mapMethods) throws IOException {
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
	public static void processDirectoryMultithreaded(Path input, ThreadSafeInvertedIndex index, WorkQueue worker)
			throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
							processDirectoryMultithreaded(entry, index, worker);
				}
				else if (isTextFile(entry)) {
					worker.execute(() -> {
						try {
							ThreadSafeInvertedIndex localIndex = new ThreadSafeInvertedIndex();
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
