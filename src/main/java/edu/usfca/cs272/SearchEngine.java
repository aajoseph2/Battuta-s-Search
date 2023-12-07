package edu.usfca.cs272;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Demonstrates how to create a simple message board using Jetty and servlets,
 * as well as how to initialize servlets when you need to call its constructor.
 */
public class SearchEngine {
	/** The hard-coded port to run this server. */
	public static final int PORT = 8080;

	/**
	 * Sets up a Jetty server with different servlet instances.
	 *
	 * @param args unused
	 * @throws Exception if unable to start and run server
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();

		handler.addServletWithMapping(new ServletHolder(new BulmaMessageServlet()), "/bulma");

		server.setHandler(handler);
		server.start();
		server.join();
	}
}
