package edu.usfca.cs272;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Demonstrates how to create a simple message board using Jetty and servlets,
 * as well as how to initialize servlets when you need to call its constructor.
 */
public class SearchEngine {

	private ThreadSafeInvertedIndex index;
	private QueryProcessorInterface queryProcessor;
	private SearchHistory searchHistory;

	public SearchEngine(ThreadSafeInvertedIndex index, QueryProcessorInterface queryProcessor,
			SearchHistory searchHistory) {
		this.index = index;
		this.queryProcessor = queryProcessor;
		this.searchHistory = searchHistory;
	}

	/**
	 * Reads a file from the classpath and returns its content as a String.
	 *
	 * @param fileName The name of the file to be read, relative to the classpath.
	 * @return The content of the file as a String.
	 * @throws IOException If an error occurs during file reading.
	 */
	public static String readResourceFile(String fileName) throws IOException {
		String resourcePath = "html/" + fileName;
		InputStream inputStream = SearchEngine.class.getClassLoader().getResourceAsStream(resourcePath);

		if (inputStream == null) {
			throw new FileNotFoundException("Resource file not found: " + resourcePath);
		}
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	/**
	 * Sets up a Jetty server with different servlet instances.
	 *
	 * @param port specified port number to run server, defaults in 8080 if no
	 *   arguement is provided.
	 * @throws Exception if unable to start and run server
	 */
	public void runServer(int port) throws Exception {
		Server server = new Server(port);

		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/");

		contextHandler.addServlet(new ServletHolder(new HomeServlet()), "/home");
		contextHandler.addServlet(new ServletHolder(new InvertedIndexServlet(index, queryProcessor)), "/index");
		contextHandler.addServlet(new ServletHolder(new SearchResultsServlet(index, queryProcessor, searchHistory)),
				"/results");
		contextHandler.addServlet(new ServletHolder(new LocationServlet(index, queryProcessor)), "/locations");
		contextHandler.addServlet(new ServletHolder(new DownloadIndexServlet(index)), "/download");
		contextHandler.addServlet(new ServletHolder(new HistoryServlet(searchHistory)), "/history");
		contextHandler.addServlet(new ServletHolder(new ClearHistoryServlet(searchHistory)), "/clearHistory");

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("/Users/aminjoseph/git/project-aajoseph2/src/main/resources/static");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, contextHandler });

		System.out.println("Starting server on port " + port);

		server.setHandler(handlers);
		server.start();
		server.join();
	}
}

//TODO reverse sort order
//TODO java doc
