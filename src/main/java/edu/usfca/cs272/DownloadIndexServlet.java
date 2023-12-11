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

public class DownloadIndexServlet extends HttpServlet {

	private InvertedIndex index;

	public DownloadIndexServlet(InvertedIndex index) {
		this.index = index;
	}

	private String readIndexJson(Path indexPath) throws IOException {
		return new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Content-Disposition", "attachment; filename=\"index.json\"");

		// chance that i am unneccarily writing into index.json twice
		Path indexPath = Paths.get("index.json");
		index.writeJson(indexPath);
		String jsonIndex = readIndexJson(indexPath);

		try (PrintWriter out = response.getWriter()) {
			out.write(jsonIndex);
		}
	}
}
