package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Amin Joseph
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * @throws IOException 
	 * 
	 */
	
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");
	
	public static String clean(String text) {
		String cleaned = Normalizer.normalize(text, Normalizer.Form.NFD);
		cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
		return cleaned.toLowerCase();
	}
	
	public static String[] split(String text) {
		return text.isBlank() ? new String[0] : SPLIT_REGEX.split(text.strip());
	}
	
	public static String[] parse(String text) {
		return split(clean(text));
	}
	

	
	
	public static int textProcess (Path input) throws IOException {
		
		StringBuilder inputText = new StringBuilder();
		 try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			 String line;
		        while ((line = reader.readLine()) != null) {
		            inputText.append(line).append("\n");
		        }
		    }
		 
		
		String[] str = parse(inputText.toString());

		System.out.println(Arrays.toString(str));
		
		return str.length;
		
	}
	
	
	
	
	public static void main(String[] args) throws IOException {
		
		 for (int i = 0; i < args.length; i++) {
             
             if (args[i].equals("-text")) {
            	 Path path = Paths.get(args[i+1]);
            	 
            	 
            	 if (Files.isDirectory(path)) {
            		System.out.println("This is a directory");
            	 } else {
            		textProcess(path);
            	 }
            
             }
     }


	}

	/*
	 * Generally, "Driver" classes are responsible for setting up and calling other
	 * classes, usually from a main() method that parses command-line parameters.
	 * Generalized reusable code are usually placed outside of the Driver class.
	 * They are sometimes called "Main" classes too, since they usually include the
	 * main() method.
	 *
	 * If the driver were only responsible for a single class, we use that class
	 * name. For example, "TaxiDriver" is what we would name a driver class that
	 * just sets up and calls the "Taxi" class.
	 *
	 * The starter code (calculating elapsed time) is not necessary. It can be
	 * removed from the main method.
	 *
	 */
}
