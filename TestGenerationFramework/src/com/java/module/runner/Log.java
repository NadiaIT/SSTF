package com.java.module.runner;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Log {

	public static void writeToFile(String filePath, String content)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		String[] lines = content.split("\n");
	    for (String line: lines) {
	    	writer.println(line);
	    }
		writer.close();
	}
}
