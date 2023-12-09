package edu.usfca.cs272;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Demonstrates how to create a simple message board using Jetty and servlets,
 * as well as how to initialize servlets when you need to call its constructor.
 */
public class SearchEngine {

	/**
	 * Sets up a Jetty server with different servlet instances.
	 *
	 * @param port specified port number to run server, defaults in 8080 if no
	 *   arguement is provided.
	 * @throws Exception if unable to start and run server
	 */
	public static void runServer(int port) throws Exception {
		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();

		handler.addServletWithMapping(new ServletHolder(new HomeServlet()), "/home");
		handler.addServletWithMapping(new ServletHolder(new InvertedIndexServlet()), "/index");
		handler.addServletWithMapping(new ServletHolder(new SearchResultsServlet()), "/results");

		server.setHandler(handler);
		server.start();
		server.join();
	}
}
