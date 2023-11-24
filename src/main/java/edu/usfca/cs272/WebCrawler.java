package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class to iterate through a list of links or link. Fetches and cleans html
 * data, then processes stemmed words into the inverted index data strcuture.
 */
public class WebCrawler {
	/**
	 * Inverted Index of which contains all the word data
	 */
	private final ThreadSafeInvertedIndex index;
	/**
	 * Set of all visited links, meant to not be visited again
	 */
	private final Set<URL> visited;
	/**
	 * The lock used to protect concurrent access to the underlying set.
	 */
	private final MultiReaderLock lock;
	/**
	 * Workers to do work utilizing several threads
	 */
	private final WorkQueue workers;

	/**
	 * @param index Inverted Index of which contains all the word data, passed
	 *   through in constuctor
	 * @param workers Workers to do work
	 */
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue workers) {
		this.index = index;
		this.visited = new HashSet<>();
		lock = new MultiReaderLock();
		this.workers = workers;
	}

	/**
	 * @param url target source of where the data is being retrieved from
	 * @param maxDepth Number of times to iterate through nested links
	 * @throws IOException if link is unreadable
	 */
	public void crawl(URL url, int maxDepth) throws IOException {
		if (maxDepth < 0 || visited.contains(url)) {
			return;
		}

		visited.add(url);

		String html = HtmlFetcher.fetch(url, 3);
		if (html != null) {
			String cleanHtml = HtmlCleaner.stripHtml(html);
			URI baseLocation = LinkFinder.cleanUri(LinkFinder.makeUri(url.toString()));
			processText(cleanHtml, baseLocation.toString());

			if (maxDepth > 1) {
				findAndCrawlLinks(url, html, maxDepth);
			}
		}
	}

	/**
	 * @param text the cleaned html to be inputted within InvertedIndex
	 * @param location Location or link of the text
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
	 * Recursively finds and crawls all unique links found within the given HTML
	 * content.
	 *
	 * @param url the base URL from which the HTML content was retrieved
	 * @param html the HTML content containing the links to be crawled
	 * @param maxDepth the maximum depth to crawl
	 * @throws IOException if an I/O error occurs while crawling the links
	 */
	private void findAndCrawlLinks(URL url, String html, int maxDepth) throws IOException {
		Set<URL> links = LinkFinder.uniqueUrls(url, html);
		for (URL nextUrl : links) {
			if (!visited.contains(nextUrl)) {
				crawl(nextUrl, maxDepth - 1);
			}
		}
	}

	/**
	 * Checks if the URL has already been visited.
	 *
	 * @param url The URL to check
	 * @return true if the URL has been visited, false otherwise
	 */
	public boolean visitedContains(URL url) {
		lock.readLock().lock();
		try {
			return visited.contains(url);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Adds a new URL to the set of visited URLs if it hasn't been visited.
	 *
	 * @param url The URL to add
	 */
	public void visitedAdd(URL url) {
		lock.writeLock().lock();
		try {
			visited.add(url);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
}
