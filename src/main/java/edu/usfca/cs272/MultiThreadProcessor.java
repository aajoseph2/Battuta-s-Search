package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
			workers.execute(() -> {
				try {
					InvertedIndex localIndex = new InvertedIndex();
					InvertedIndexProcessor.processText(path, localIndex);
					index.addDistinctIndex(localIndex);
				}
				catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}

		workers.finish();
	}

	/**
	 * Multi threaded version of processDirectory
	 *
	 * @param input the directory
	 * @param index contains the structure for the read data
	 * @param workers workers threads to do job
	 * @throws IOException If file is unable to be read, then throw an exception.
	 */
	public static void processDirectoryMultithreaded(Path input, ThreadSafeInvertedIndex index, WorkQueue workers)
			throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectoryMultithreaded(entry, index, workers);
				}
				else if (InvertedIndexProcessor.isTextFile(entry)) {
					workers.execute(() -> {
						try {
							InvertedIndex localIndex = new InvertedIndex();
							InvertedIndexProcessor.processText(entry, localIndex);
							index.addDistinctIndex(localIndex);
						}
						catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
				}
			}
		}
	}

}
