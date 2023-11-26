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

	public Crawler(ThreadSafeInvertedIndex index, int maxCrawlLimit) {
		this.index = index;
		this.MAX_CRAWL_LIMIT = maxCrawlLimit;
	}

	public void startCrawl(URL seedUrl) throws IOException {
		urlQueue.add(seedUrl);
		while (!urlQueue.isEmpty() && crawledCount < MAX_CRAWL_LIMIT) {
			if (crawledCount >= MAX_CRAWL_LIMIT) {
				break;
			}
			URL currentUrl = urlQueue.poll();
			if (!visitedUrls.contains(currentUrl)) {
//          	System.out.println(crawledCount);
//          	System.out.println(currentUrl);
				crawl(currentUrl);
			}
		}
	}

	private void crawl(URL url) throws IOException {
		if (crawledCount >= MAX_CRAWL_LIMIT) {
			return;
		}
		visitedUrls.add(url);
		crawledCount++;

		String html = HtmlFetcher.fetch(url, 3);
		if (html != null) {
			// System.out.println(url);
			String cleanHtml = HtmlCleaner.stripHtml(html);
			processText(cleanHtml, LinkFinder.cleanUri(LinkFinder.makeUri(url.toString())).toString());
			if (crawledCount < MAX_CRAWL_LIMIT) {
				processLinks(url, html);
			}
		}
		else {
			// crawledCount--;
		}
	}

	private void processLinks(URL url, String html) throws IOException {
		var links = LinkFinder.listUrls(url, html);
		// System.out.println(links.size());
		// System.out.println(links + "\n");
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
