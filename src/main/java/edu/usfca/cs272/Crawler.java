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
	private final MultiReaderLock lock;

	public Crawler(ThreadSafeInvertedIndex index, int maxCrawlLimit, WorkQueue workers) {
		this.index = index;
		this.MAX_CRAWL_LIMIT = maxCrawlLimit;
		this.workers = workers;
		this.lock = new MultiReaderLock();
	}

	public void startCrawl(URL seedUrl) {
		TaskManager manager = new TaskManager();
		manager.start(seedUrl);
		//need ot finish
	}

	private class TaskManager {
		private void start(URL url) {
			synchronized (lock) {
				if (visitedContains(url) || crawledCount >= MAX_CRAWL_LIMIT) {
					return;
				}

				visitedAdd(url);
				crawledCount++;
			}

//			private void start(Path path) {
//				Thread worker = new Worker(path);
//				worker.start();
//			}
			workers.execute(new Worker(url));
		}

		private void finish() {
			workers.finish();
		}
	}

	private void crawl(URL url) throws IOException {
		if (visitedContains(url) || getCrawledCount() >= MAX_CRAWL_LIMIT) {
			return;
		}

		visitedAdd(url);
		crawledCountIncrement();

		String html = HtmlFetcher.fetch(url, 3);
		if (html != null) {
			String cleanHtml = HtmlCleaner.stripHtml(html);
			processText(cleanHtml, LinkFinder.cleanUri(LinkFinder.makeUri(url.toString())).toString());

			var links = LinkFinder.listUrls(url, html);
			for (URL nextUrl : links) {
				crawl(nextUrl);
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

	public void urlQueueAdd(URL link) {
		lock.writeLock().lock();
		try {
			urlQueue.add(link);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	public URL urlQueuePoll() {
		lock.readLock().lock();
		try {
			return urlQueue.poll();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	public boolean urlQueueIsEmpty() {
		lock.readLock().lock();
		try {
			return urlQueue.isEmpty();
		}
		finally {
			lock.readLock().unlock();
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

	public void crawledCountIncrement() {
		lock.writeLock().lock();
		try {
			crawledCount++;
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	public int getCrawledCount() {
		lock.readLock().lock();
		try {
			return crawledCount;
		}
		finally {
			lock.readLock().unlock();
		}
	}

}
