package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.TreeMap;
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
	
	public static TreeMap<Path,Integer> fileInfo = new TreeMap<>();
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
	

	public static void iterDirectory(Path input) {
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
	        for (Path entry : stream) {
	            if (entry.getFileName().toString().equals(".DS_Store")) {
	                continue;  
	            }
	            if (Files.isDirectory(entry)) {
	                iterDirectory(entry);
	            } else {
	            	String fileNameLower = entry.getFileName().toString().toLowerCase();
	            	if(fileNameLower.endsWith(".txt") || fileNameLower.endsWith(".text")) {
	                    try {
	                        textProcess(entry);
	                    } catch (MalformedInputException e) {
	                        System.out.println("Skipped due to encoding issues: " + entry);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        }
	    } catch (IOException e) {
	    	System.out.println("File was not able to be read!");
	        e.printStackTrace();
	        //System.out.println("File was not able to be read!");
	    }
	}


	
	
	public static void textProcess(Path input) throws IOException {
		
		if(!Files.exists(input)) {
	        System.out.println("Invalid file: " + input.toString());
	        return;
	    }
		
		StringBuilder inputText = new StringBuilder();
		 try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			 String line;
		        while ((line = reader.readLine()) != null) {
		            inputText.append(line).append("\n");
		        }
		    }
		
		Path rootPath = Paths.get("").toAbsolutePath();
		Path relative = rootPath.relativize(input);
		

		String[] str = parse(inputText.toString());	
		
		if (str.length != 0) {
			fileInfo.put(relative, str.length);
		}
		
		
	}
	
	public static Path toAbsolutePath(Path path, String relativePathString) {
	    if (!path.isAbsolute()) {
	        Path currentWorkingDir = Paths.get("").toAbsolutePath();
	        path = Paths.get(currentWorkingDir.toString(), relativePathString);
	    }
	    return path;
	}
	
	
	
	public static String mapToJson() {       
	    StringBuilder json = new StringBuilder("{\n");
	    

	    for (var entry : fileInfo.entrySet()) {
	        json.append("  \"")
	            .append(entry.getKey())
	            .append("\": ")
	            .append(entry.getValue())
	            .append(",\n");
	    }       

	    if (json.length() > 2) {
	        json.setLength(json.length() - 2);
	    } else {
	        return "{\n}";
	    }
	    
	    json.append("\n}");
	    return json.toString();
	}

	
	public static void writeJsonToFile(String json, Path outputPath) {
        try {
            Files.write(outputPath, json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	
	public static void main(String[] args) throws IOException {
	    
		for (int i = 0; i < args.length; i++) {
	        int bound = args.length;

	        if (args[i].equals("-text")) {
	            fileInfo.clear();
	            if ((i + 1 >= bound)) {
	                System.out.println("Missing file path to read!\n");
	                continue;
	            } else {
	                Path path = Paths.get(args[i+1]);
	                path = toAbsolutePath(path, args[i+1]);
	                if (Files.isDirectory(path)) {
	                    iterDirectory(path);
	                } else {
	                    textProcess(path);
	                }
	                i++;
	            }
	        } else if (args[i].equals("-counts")) {
	            if ((i + 1 >= bound) || (args[i+1].startsWith("-"))) {
	                Path countPath = Paths.get("counts.json");
	                writeJsonToFile(mapToJson(), countPath);
	            } else {
	                Path countPath = Paths.get(args[i+1]);
	                countPath = toAbsolutePath(countPath, args[i+1]);
	                writeJsonToFile(mapToJson(), countPath);
	                i++;
	            }
	        } else if (args[i].equals("-index")) {
	        	//Indexing not done,for now has the same functionality as counts
	            if ((i + 1 >= bound) || (args[i+1].startsWith("-"))) {
	                Path indexPath = Paths.get("index.json");
	                writeJsonToFile(mapToJson(), indexPath);
	            } else {
	                Path indexPath = Paths.get(args[i+1]);
	                indexPath = toAbsolutePath(indexPath, args[i+1]);
	                writeJsonToFile(mapToJson(), indexPath);
	                i++;
	            }
	        } else {
	            System.out.println("Ignoring unknown argument: " + args[i]);
	        }
	    }
	}

}
