package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InvertedIndexServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String jsonIndex = getIndexAsJsonString();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print(jsonIndex);
		out.flush();
	}

	private String getIndexAsJsonString() {
		return YourIndexClass.getJsonStringOfIndex();
	}
}
