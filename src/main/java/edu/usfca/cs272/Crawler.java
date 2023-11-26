package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class Crawler {


	private final ThreadSafeInvertedIndex index;

	private final LinkedHashSet<URL> visited;


	private int queuedURLs;

	private URL rootUrl;
	private int depth;

	/**
	 * @param index Inverted Index of which contains all the word data, passed
	 *   through in constuctor
	 * @param workers Workers to do work
	 */
	public Crawler(ThreadSafeInvertedIndex index, WorkQueue workers, URL rootUrl, int depth) {
		this.index = index;
		this.visited = new LinkedHashSet<>();
		this.queuedURLs = 1;
		this.rootUrl = rootUrl;
		this.depth = depth;
	}

	/**
	 * Header method to call the processing methods for pages
	 *
	 * @param url url target source of where the data is being retrieved from
	 * @param maxDepth maxDepth Number of times to iterate through nested links
	 * @throws IOException IOException if link is unreadable
	 */
	public void crawl(URL url, int maxDepth) throws IOException {
		visited.add(url);
		String html = HtmlFetcher.fetch(url, 3);
		if (html != null) {
			String cleanHtml = HtmlCleaner.stripHtml(html);
			var links = LinkFinder.listUrls(url, html);
			for (URL nextUrl : links) {
				visited.add(nextUrl);
			}
		} else {
			System.out.println("Root link has no valid html");
		}

		System.out.println(visited);

	}

	/**
	 * @param url target source of where the data is being retrieved from
	 * @param maxDepth Number of times to iterate through nested links
	 * @throws IOException if link is unreadable
	 */
	public void processPage(URL url, int maxDepth) throws IOException {

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
	private void processLinks(URL url, String html, int maxDepth) throws IOException {
		if (url.equals(rootUrl)) {
			var links = LinkFinder.listUrls(url, html);
			for (URL nextUrl : links) {
				//depth--;
				depth--;
				System.out.println("depth: " + depth);
				crawl(nextUrl, depth);
				//maxDepth--;
				//System.out.println("crawl: " + maxDepth);
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

}
