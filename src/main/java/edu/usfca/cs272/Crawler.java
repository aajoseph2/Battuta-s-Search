package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class Crawler {

	private final ThreadSafeInvertedIndex index;

	private final ArrayList<URL> links;

	private int queuedURLs;
	private final LinkedHashSet<URL> visited;
	private URL rootUrl;
	private int depth;

	/**
	 * @param index Inverted Index of which contains all the word data, passed
	 *   through in constuctor
	 * @param workers Workers to do work
	 */
	public Crawler(ThreadSafeInvertedIndex index, WorkQueue workers, URL rootUrl, int depth) {
		this.index = index;
		this.links = new ArrayList<URL>();
		this.visited = new LinkedHashSet<>();
		this.queuedURLs = 0;
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
		links.add(url);
		String html = HtmlFetcher.fetch(url, 3);
			links.addAll(LinkFinder.listUrls(url, html));
			//System.out.println(visited.size());

		//System.out.println(visited);
		processLinks(links, maxDepth);
	}

	private void processLinks(ArrayList<URL> links, int maxDepth) throws IOException {
		for (URL nextUrl : links) {
			if (maxDepth <= 0 || queuedURLs >= 50 || visited.contains(links)) {
				continue;
			}
			visited.add(nextUrl);
			String html = HtmlFetcher.fetch(nextUrl, 3);
			if (html != null) {
				String cleanHtml = HtmlCleaner.stripHtml(html);
				processText(cleanHtml, LinkFinder.cleanUri(LinkFinder.makeUri(nextUrl.toString())).toString());
				maxDepth--;
			}
			queuedURLs++;
		}


//		if (maxDepth > 0) {
//			crawl(links.get(1), maxDepth);
//			visited.remove(1);
//			}
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
