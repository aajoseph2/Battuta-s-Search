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
	private final InvertedIndex index;
	/**
	 * Set of all visited links, meant to not be visited again
	 */
	private final Set<URL> visited;

	/**
	 * @param index Inverted Index of which contains all the word data, passed
	 *   through in constuctor
	 */
	public WebCrawler(InvertedIndex index) {
		this.index = index;
		this.visited = new HashSet<>();
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
		}

		// also track the searched links
		// decrement depth for 4.1 tests to find links within the HTML and recursively
		// call crawl() on those URLs
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
