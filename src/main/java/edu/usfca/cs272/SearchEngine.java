package edu.usfca.cs272;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Demonstrates how to create a simple message board using Jetty and servlets,
 * as well as how to initialize servlets when you need to call its constructor.
 */
public class SearchEngine {

	private ThreadSafeInvertedIndex index;
	private QueryProcessorInterface queryProcessor;

	public SearchEngine(ThreadSafeInvertedIndex index, QueryProcessorInterface queryProcessor) {
		this.index = index;
		this.queryProcessor = queryProcessor;
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

		// Use ServletContextHandler to allow passing objects to servlets
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/");
		server.setHandler(contextHandler);

		// Passing the shared index and queryProcessor instances to the
		// SearchResultsServlet
		contextHandler.addServlet(new ServletHolder(new HomeServlet()), "/home");
		contextHandler.addServlet(new ServletHolder(new InvertedIndexServlet(index, queryProcessor)), "/index");
		contextHandler.addServlet(new ServletHolder(new SearchResultsServlet(index, queryProcessor)), "/results");

		server.start();
		server.join();
	}
}
