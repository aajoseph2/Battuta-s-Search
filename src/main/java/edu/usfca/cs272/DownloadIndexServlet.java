package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A servlet that facilitates the downloading of the inverted index in JSON
 * format. It generates a JSON representation of the inverted index and sends it
 * as a downloadable file.
 */
public class DownloadIndexServlet extends HttpServlet {

	/**
	 * Default serial (unused)
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Instance of the InvertedIndex class used to obtain data for the downloaed
	 * index file.
	 */
	private InvertedIndex index;

	/**
	 * Constructs a DownloadIndexServlet with the specified inverted index.
	 *
	 * @param index The InvertedIndex object that will be used to generate the JSON
	 *   representation.
	 */
	public DownloadIndexServlet(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * Reads the content of the file at the given path and returns it as a string.
	 * This method is used to read the contents of the generated index.json file.
	 *
	 * @param indexPath The path to the file that needs to be read.
	 * @return A string representation of the file's contents.
	 * @throws IOException If an I/O error occurs reading from the file or a
	 *   malformed or unmappable byte sequence is read.
	 */
	private String readIndexJson(Path indexPath) throws IOException {
		return new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
	}

	/**
	 * Handles the GET request by generating a JSON representation of the inverted
	 * index and setting up the response for file download. The file is named
	 * "index.json".
	 *
	 * @param request object that contains the request the client made of the
	 *   servlet.
	 * @param response object that contains the response the servlet returns to the
	 *   client.
	 * @throws IOException occurs during the creation of the JSON file or sending
	 *   the response.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Content-Disposition", "attachment; filename=\"index.json\"");

		Path indexPath = Paths.get("index.json");
		index.writeJson(indexPath);
		String jsonIndex = readIndexJson(indexPath);

		try (PrintWriter out = response.getWriter()) {
			out.write(jsonIndex);
		}
	}
}
