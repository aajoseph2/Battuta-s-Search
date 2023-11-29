package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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
	private final int MAX_CRAWL_LIMIT;
	private final Set<URL> visitedUrls = new HashSet<>();
	private final Queue<URL> urlQueue = new LinkedList<>();
	private int crawledCount = 0;
	/**
	 * Workers useded in task manager, where one worker will hadle one url.
	 */
	private final WorkQueue workers;
	/**
	 * The lock used to protect concurrent access to the underlying set.
	 */
	private final MultiReaderLock lock;

	public Crawler(ThreadSafeInvertedIndex index, int maxCrawlLimit, WorkQueue workers) {
		this.index = index;
		this.MAX_CRAWL_LIMIT = maxCrawlLimit;
		this.workers = workers;
		this.lock = new MultiReaderLock();
	}

	public void startCrawl(URL seedUrl) {
		if (seedUrl != null) {
			submitTask(seedUrl);
		}
		workers.finish();
	}

	private void submitTask(URL url) {
		synchronized (lock) {
			if (visitedContains(url) || crawledCount >= MAX_CRAWL_LIMIT) {
				return;
			}
			visitedAdd(url);
			crawledCount++;
		}
		workers.execute(new Worker(url));
	}

	private class Worker implements Runnable {
		private final URL url;

		public Worker(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {
				crawl(url);
			}
			catch (IOException e) {
				System.out.println("Error encountered while running " + e.getMessage());
			}
		}
	}

	private void crawl(URL url) throws IOException {
		String html = HtmlFetcher.fetch(url, 3);
		if (html != null) {
			String cleanHtml = HtmlCleaner.stripHtml(html);
			processText(cleanHtml, LinkFinder.cleanUri(LinkFinder.makeUri(url.toString())).toString());
			var links = LinkFinder.listUrls(url, html);
			for (URL nextUrl : links) {
				submitTask(nextUrl);
			}
		}
	}

	private void processText(String text, String location) {
		int pos = 1;
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		String[] words = TextParser.parse(text);
		for (String word : words) {
			index.addData(stemmer.stem(word).toString(), location, pos);
			pos++;
		}
	}

	public void visitedAdd(URL url) {
		lock.writeLock().lock();
		try {
			visitedUrls.add(url);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

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
