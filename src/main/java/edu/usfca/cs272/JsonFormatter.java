package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 */

public class JsonFormatter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {

		writer.write("[\n");

		Iterator<? extends Number> iterator = elements.iterator();

		if (iterator.hasNext()) {
			Number element = iterator.next();

			writeIndent(writer, indent + 1);
			writer.write(element.toString());
		}

		while (iterator.hasNext()) {
			writer.write(",");
			writer.write("\n");

			Number element = iterator.next();
			writeIndent(writer, indent + 1);
			writer.write(element.toString());
		}

		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("]");
	}

	/*
	 * TODO public static void writeArray2(Collection<? extends Number> elements,
	 * Writer writer, int indent) throws IOException { writer.write("["); Iterator<?
	 * extends Number> iterator = elements.iterator();
	 *
	 * if (iterator.hasNext()) { writeArrayEntry(...); }
	 *
	 * while (iterator.hasNext()) { writer.write(","); writeArrayEntry(...); }
	 *
	 * writer.write("\n"); writeIndent(writer, indent); writer.write("]"); }
	 *
	 * public static void writeArrayEntry(...) ... { Number element =
	 * iterator.next(); writer.write("\n"); writeIndent(writer, indent + 1);
	 * writer.write(element.toString()); }
	 */

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent) throws IOException {
		// TODO Use the if/while approach in all of these methods
		writer.write("{\n");

		Iterator<? extends Map.Entry<String, ? extends Number>> iterator = elements.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ? extends Number> entry = iterator.next();
			String elementString = entry.getKey();
			Number elementNum = entry.getValue();

			writeIndent(writer, indent + 1);
			writer.write('"');
			writer.write(elementString);
			writer.write("\": ");
			writer.write(elementNum.toString());

			if (iterator.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}

		writeIndent(writer, indent);
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @return String
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("{\n");

		var iterator = elements.entrySet().iterator();

		while (iterator.hasNext()) {
			var entry = iterator.next();

			String elementString = entry.getKey();
			Collection<? extends Number> elementCollection = entry.getValue();

			writer.write("  ");
			writeQuote(elementString, writer, indent + 1);
			writer.write(": ");

			if (elementCollection != null && !elementCollection.isEmpty()) {
				writeArray(elementCollection, writer, indent + 2);
			}
			else {
				writer.write("[\n");
				writeIndent(writer, indent + 1);
				writer.write("]");
			}

			if (iterator.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}

		writeIndent(writer, indent);

		return writer.toString();
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("[\n");

		var iterator = elements.iterator();

		while (iterator.hasNext()) {
			var element = iterator.next();

			writeIndent(writer, indent + 1);

			writeObject(element, writer, indent + 1);

			if (iterator.hasNext()) {
				writer.write(",");
			}

			writer.write("\n");
		}

		writeIndent(writer, indent);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Converts the formatted map, into json pretty text that follows the standard
	 * of the example from the readMe.
	 *
	 * @param index index map filled with data
	 * @param writer String appender to be parsed into json format
	 * @param indent indent increment number
	 * @return pretty json string
	 * @throws IOException if file is unreadable
	 */
	// TODO Set should be Collection, return void
	public static String writeIndexJson(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index,
			Writer writer, int indent) throws IOException {

		var iterator = index.entrySet().iterator();

		writeIndent("{\n", writer, 0);

		while (iterator.hasNext()) {
			var entry = iterator.next();
			String stem = entry.getKey();

			// TODO JsonFormatter.writeObjectArrays(entry.getValue(), writer, indent + 1);
			String loc = JsonFormatter.writeObjectArrays(entry.getValue());

			writeQuote(stem, writer, indent);
			writer.write(": ");
			writer.write(loc.toString());

			writeIndent("}", writer, indent);

			if (iterator.hasNext()) {
				writer.write(",");
				writeIndent("\n", writer, indent - 1);
			}
			else {
				writeIndent("\n", writer, indent - 1);
			}
		}

		writeIndent("}", writer, indent - 1);

		return writer.toString();
	}

	/**
	 * @param index map filled with data
	 * @param path path the file path to use
	 * @param indent indent increment number
	 * @return wrapper to stringBuilder.toString() that is ready to be inputted
	 *   within writeJsonToFile()
	 * @throws IOException if file is unreable
	 */
	// TODO return void
	public static String writeIndexJson(Map<String, ? extends Map<String, ? extends Set<? extends Number>>> index,
			Path path, int indent) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			return writeIndexJson(index, writer, indent);
		}
	}

	/**
	 * @param mapMethods contains the structure for the read data
	 * @return wrapper to stringBuilder.toString() that is ready to be inputted
	 *   within writeJsonToFile()
	 * @throws IOException if file is unreable
	 */
	public static String writeIndexJson(InvertedIndex mapMethods) throws IOException {
		var formatMap = mapMethods.constructIndexRepresentation();
		StringWriter buffer = new StringWriter();
		return writeIndexJson(formatMap, buffer, 1);
	}

	/**
	 * @param results The data structure containing the search data
	 * @return string of formatted json
	 * @throws IOException if file is unreadable
	 */
	public static String writeSearchResults(Map<String, List<SearchResult>> results) throws IOException {
		StringWriter writer = new StringWriter();

		writer.write("{\n");
		Iterator<Map.Entry<String, List<SearchResult>>> iterator = results.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, List<SearchResult>> entry = iterator.next();

			writeQuote(entry.getKey(), writer, 1);
			writer.write(": [\n");

			List<SearchResult> searchResults = entry.getValue();
			for (int i = 0; i < searchResults.size(); i++) {
				SearchResult result = searchResults.get(i);

				writeIndent("{\n", writer, 2);

				writeQuote("count", writer, 3);
				writer.write(": " + result.getCount() + ",\n");

				writeIndent("", writer, 3);
				writeQuote("score", writer, 0);
				writer.write(": " + new DecimalFormat("0.00000000").format(result.getScore()) + ",\n");

				writeIndent("", writer, 3);
				writeQuote("where", writer, 0);
				writer.write(": " + "\"" + result.getWhere() + "\"\n");

				writeIndent("}", writer, 2);

				if (i < searchResults.size() - 1) {
					writer.write(",");
				}

				writer.write("\n");
			}

			writeIndent("]", writer, 1);

			if (iterator.hasNext()) {
				writer.write(",");
			}

			writer.write("\n");
		}

		writer.write("}\n");
		return writer.toString();
	}

	/*
	 * TODO public static String writeIndexJson(Map<String, ? extends Map<String, ?
	 * extends Set<? extends Number>>> index, Path path, int indent) throws
	 * IOException { try (StringWriter writer = new StringWriter()) {
	 * writeIndexJson(index, writer, indent); return writer.toString(); } }
	 */

}
