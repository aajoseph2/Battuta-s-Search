package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LocationServlet extends HttpServlet {

	private InvertedIndex index;
	private QueryProcessorInterface queryProcessor;
	private final String locationTemplate;

	public LocationServlet(ThreadSafeInvertedIndex index, QueryProcessorInterface queryProcessor)
			throws IOException {
		this.index = index;
		this.queryProcessor = queryProcessor;
		locationTemplate = SearchEngine.readResourceFile("Locations.html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print(buildHtmlLocations());
		out.flush();
	}

	private String buildHtmlLocations() {
		StringBuilder builder = new StringBuilder();

		System.out.println("logic to get locations from couts");

		return locationTemplate.replace("${title}", "Battuta's Search")
				.replace("${results}", builder.toString());
	}
}
