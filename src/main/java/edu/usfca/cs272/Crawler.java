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

public class Crawler {

	private final ThreadSafeInvertedIndex index;
	private final int MAX_CRAWL_LIMIT;
	private final Set<URL> visitedUrls = new HashSet<>();
	private final Queue<URL> urlQueue = new LinkedList<>();
	private int crawledCount = 0;
	private final WorkQueue workers;


	public Crawler(ThreadSafeInvertedIndex index, int maxCrawlLimit, WorkQueue workers) {
		this.index = index;
		this.MAX_CRAWL_LIMIT = maxCrawlLimit;
		this.workers = workers;
	}

	public void startCrawl(URL seedUrl) throws IOException {
		urlQueue.add(seedUrl);
		while (!urlQueue.isEmpty() && crawledCount < MAX_CRAWL_LIMIT) {
			URL currentUrl = urlQueue.poll();
			if (!visitedUrls.contains(currentUrl)) {
				crawl(currentUrl);
			}
		}
	}

	private void crawl(URL url) throws IOException {
		visitedUrls.add(url);
		crawledCount++;

		String html = HtmlFetcher.fetch(url, 3);
		if (html != null) {
			String cleanHtml = HtmlCleaner.stripHtml(html);
			processText(cleanHtml, LinkFinder.cleanUri(LinkFinder.makeUri(url.toString())).toString());
			if (crawledCount < MAX_CRAWL_LIMIT) {
				processLinks(url, html);
			}
		}
	}

	private void processLinks(URL url, String html) throws IOException {
		var links = LinkFinder.listUrls(url, html);
		for (URL nextUrl : links) {
			if (!visitedUrls.contains(nextUrl)) {
				urlQueue.add(nextUrl);
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
}
