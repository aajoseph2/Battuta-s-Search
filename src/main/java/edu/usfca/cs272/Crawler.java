package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class Crawler {

	private final ThreadSafeInvertedIndex index;

	private final ArrayList<URL> links;
	private int crawledURLsCount = 0;
	private Set<URL> visited = new HashSet<>();
	private Queue<URLDepthPair> queue = new LinkedList<>();
	private int queuedURLs = 0;
	private final int MAX_CRAWL_LIMIT;

	/**
	 * @param index Inverted Index of which contains all the word data, passed
	 *   through in constuctor
	 * @param workers Workers to do work
	 */
	public Crawler(ThreadSafeInvertedIndex index, WorkQueue workers, URL rootUrl, int maxCrawlLimit) {
		this.index = index;
		this.links = new ArrayList<URL>();
		this.visited = new LinkedHashSet<>();
		this.queuedURLs = 0;
		this.MAX_CRAWL_LIMIT = Math.min(maxCrawlLimit, 49);
		// this.rootUrl = rootUrl;
		// this.depth = depth;
	}

	private static class URLDepthPair {
		URL url;
		int depth;

		URLDepthPair(URL url, int depth) {
			this.url = url;
			this.depth = depth;
		}
	}

	public void startCrawl(URL url, int maxDepth) throws IOException {
		// Add the first URL to the queue and increment the count

			queue.add(new URLDepthPair(url, maxDepth));
			visited.add(url);
			crawledURLsCount++;
		while (!queue.isEmpty()) {
			URLDepthPair pair = queue.poll();
			crawl(pair.url, pair.depth);
		}
	}

	private void crawl(URL url, int currentDepth) throws IOException {

		System.out.println("crawl:" + crawledURLsCount );
		String html = HtmlFetcher.fetch(url, 3);

		if (html != null) {
			String cleanHtml = HtmlCleaner.stripHtml(html);
			URI baseLocation = LinkFinder.cleanUri(LinkFinder.makeUri(url.toString()));
			processText(cleanHtml, baseLocation.toString());

			processLinks(url, html, currentDepth);
		}
	}

	private void processLinks(URL url, String html, int nextDepth) throws IOException {
		var links = LinkFinder.listUrls(url, html);
		for (URL nextUrl : links) {
			if  (visited.contains(nextUrl) ||crawledURLsCount > MAX_CRAWL_LIMIT ) {
				//System.out.println("here");
				continue;
			}

				visited.add(nextUrl);
				queue.add(new URLDepthPair(nextUrl, nextDepth));
				crawledURLsCount++;
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

}
