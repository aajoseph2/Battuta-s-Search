package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A web crawler that crawls web pages starting from a seed URL up to a
 * specified crawl limit.It uses a thread-safe inverted index for storing and
 * searching text data.
 */
public class Crawler {

	/**
	 * Thread safe version of the inverted index data structure.
	 */
	private final ThreadSafeInvertedIndex index;
	/**
	 * Represents the maximum number of times the webcrawler is going to crawl
	 * through Urls
	 */
	private final int maxCrawlLimit;
	/**
	 * A set of URLs that have already been visited to avoid redundant crawling.
	 */
	private final Set<URL> visitedUrls = new HashSet<>();
	/**
	 * The count of URLs that have been crawled so far. This is used to ensure that
	 * the crawler does not exceed the maximum number of URLs it is supposed to
	 * crawl.
	 */
	private int crawledCount = 0;
	/**
	 * Workers useded in task manager, where one worker will hadle one url.
	 */
	private final WorkQueue workers;
	/**
	 * The lock used to protect concurrent access to the underlying set.
	 */
	private final MultiReaderLock lock;

	/**
	 * Constructor for Crawler class. Initalizes instance of thread safe index,
	 * crawl limit, and the workers used for multithreading
	 *
	 * @param index thread safe inverted index
	 * @param maxCrawlLimit max crawl limit to be processed through
	 * @param workers Utilized for each url being processed
	 */
	public Crawler(ThreadSafeInvertedIndex index, int maxCrawlLimit, WorkQueue workers) {
		this.index = index;
		this.maxCrawlLimit = maxCrawlLimit;
		this.workers = workers;
		this.lock = new MultiReaderLock();
	}

	/**
	 * Starts crawling from the specified seed URL.
	 *
	 * @param seedUrl The starting URL for the crawl.
	 */
	public void startCrawl(URL seedUrl) {
		if (seedUrl != null) {
			submitTask(seedUrl);
		}

		workers.finish();
	}

	/**
	 * Submits a new task to crawl the specified URL if it hasn't been visited and
	 * the total number of crawled URLs is below the maximum limit.
	 *
	 * @param url The URL to be crawled.
	 */
	private void submitTask(URL url) {
		if (visitedContains(url) || crawledCount >= maxCrawlLimit) {
			return;
		}

		visitedAdd(url);
		crawledCount++;

		workers.execute(new Worker(url));
	}

	/**
	 * Represents a worker task for crawling a single URL. Implements Runnable for
	 * execution in a thread.
	 */
	private class Worker implements Runnable {
		/**
		 * The URL to be crawled by this worker.
		 */
		private final URL url;

		/**
		 * Constructs a Worker task for crawling the specified URL.
		 *
		 * @param url The URL that this worker will crawl.
		 */
		public Worker(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {
				crawl(url);
			}
			catch (IOException e) {
				System.err.println("Error encountered while crawling " + url + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Crawls the specified URL, processing its content and following any links
	 * found within it.
	 *
	 * @param url The URL to crawl.
	 * @throws IOException If an I/O error occurs during the crawling process.
	 */
	private void crawl(URL url) throws IOException {
		String html = HtmlFetcher.fetch(url, 3);

		if (html != null) {
			processText(HtmlCleaner.stripHtml(html), url.toString());
			for (URL nextUrl : LinkFinder.listUrls(url, html)) {
				submitTask(nextUrl);
			}
		}
	}

	/**
	 * Processes the text extracted from a URL, adding the stemmed words to the
	 * index along with their location and position.
	 *
	 * @param text The text to process.
	 * @param location The URL from which the text was extracted.
	 */
	private void processText(String text, String location) {
		int pos = 1;
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		String[] words = TextParser.parse(text);
		for (String word : words) {
			index.addData(stemmer.stem(word).toString(), location, pos);
			pos++;
		}
	}

	/**
	 * Adds a URL to the set of visited URLs. This method is thread-safe.
	 *
	 * @param url The URL to add to the set of visited URLs.
	 */
	public void visitedAdd(URL url) {
		lock.writeLock().lock();
		try {
			visitedUrls.add(url);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Checks if a URL has been visited. This method is thread-safe.
	 *
	 * @param url The URL to check.
	 * @return true if the URL has been visited, false otherwise.
	 */
	public boolean visitedContains(URL url) {
		lock.readLock().lock();
		try {
			return visitedUrls.contains(url);
		}
		finally {
			lock.readLock().unlock();
		}
	}
}
