package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InvertedIndexServlet extends HttpServlet {

	private InvertedIndex index;
	private QueryProcessorInterface queryProcessor;
	private final String indexTemplate;

	public InvertedIndexServlet(ThreadSafeInvertedIndex index, QueryProcessorInterface queryProcessor)
			throws IOException {
		this.index = index;
		this.queryProcessor = queryProcessor;
		indexTemplate = SearchEngine.readResourceFile("Index.html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print(buildHtmlIndex());
		out.flush();
	}

	private String buildHtmlIndex() {
		StringBuilder builder = new StringBuilder();

		for (String word : new TreeSet<>(index.getWords())) {
			builder.append("<li>").append(word).append(":<ul style='margin-left: 25px;'>");
			for (String location : index.getLocations(word)) {
				int frequency = index.numWordFrequencyAtLocation(word, location);
				builder.append("<li>")
						.append("<a href=\"")
						.append(location)
						.append("\">")
						.append(location)
						.append("</a> - Count: ")
						.append(frequency)
						.append("</li>");
			}

			builder.append("</ul></li>");
		}

		return indexTemplate.replace("${title}", "Battuta's Search").replace("${results}", builder.toString());
	}
}
