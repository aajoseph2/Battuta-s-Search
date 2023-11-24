package edu.usfca.cs272;

import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Cleans simple, validating HTML 4/5 into plain text. For simplicity, this
 * class cleans already validating HTML, it does not validate the HTML itself.
 * For example, the {@link #stripEntities(String)} method removes HTML entities
 * but does not check that the removed entity was valid.
 *
 * @see String#replaceAll(String, String)
 * @see Pattern#DOTALL
 * @see Pattern#CASE_INSENSITIVE
 * @see StringEscapeUtils#unescapeHtml4(String)
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class HtmlCleaner {
	/**
	 * Replaces all HTML tags with an empty string. For example, the html
	 *
	 *
	 * @param html text including HTML tags to remove
	 * @return text without any HTML tags
	 *
	 * @see String#replaceAll(String, String)
	 */
	public static String stripTags(String html) {
		String regex = "<[^>]+>";
		return html.replaceAll(regex, "");
	}

	/**
	 * Replaces all HTML 4 entities with their Unicode character equivalent or, if
	 *
	 * @see StringEscapeUtils#unescapeHtml4(String)
	 * @see String#replaceAll(String, String) s
	 * @param html text including HTML entities to remove
	 * @return text with all HTML entities converted or removed
	 */
	// . wild card
	public static String stripEntities(String html) {
		String unescapedHtml = StringEscapeUtils.unescapeHtml4(html);
		String regex = "&[a-zA-Z0-9x]+;";
		return unescapedHtml.replaceAll(regex, "");
	}

	/**
	 * Replaces all HTML comments with an empty string. For example:
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 *
	 * @see String#replaceAll(String, String)
	 */
	public static String stripComments(String html) {
		String regex = "(?s)<!--.*?-->";
		return html.replaceAll(regex, "");
	}

	/**
	 * Replaces everything between the element tags and the element tags themselves
	 * with an empty string. For example, consider the html code:

	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 *
	 * @see String#formatted(Object...)
	 * @see String#format(String, Object...)
	 * @see String#replaceAll(String, String)
	 */
	public static String stripElement(String html, String name) {
		String regex = "(?is)<" + Pattern.quote(name) + "(\\s+[^>]*?)?>.*?</" + Pattern.quote(name) + ">";
		return html.replaceAll(regex, "");
	}

	/**
	 * Removes comments and certain block elements from the provided html. The block
	 * elements removed include: head, style, script, noscript, iframe, and svg.
	 *
	 * @param html the HTML to strip comments and block elements from
	 * @return text clean of any comments and certain HTML block elements
	 */
	public static String stripBlockElements(String html) {
		html = stripComments(html);
		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
		html = stripElement(html, "noscript");
		html = stripElement(html, "iframe");
		html = stripElement(html, "svg");
		return html;
	}

	/**
	 * Removes all HTML tags and certain block elements from the provided text.
	 *
	 * @see #stripBlockElements(String)
	 * @see #stripTags(String)
	 *
	 * @param html the HTML to strip tags and elements from
	 * @return text clean of any HTML tags and certain block elements
	 */
	public static String stripHtml(String html) {
		html = stripBlockElements(html);
		html = stripTags(html);
		html = stripEntities(html);
		return html;
	}
}
