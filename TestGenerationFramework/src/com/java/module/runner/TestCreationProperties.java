package com.java.module.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestCreationProperties {

	public boolean compareClass;
	public boolean compareMethod;
	public String projectPath;
	public String xmlPath;
	public String testPackage;
	private String propertyPath;
	private BufferedReader reader;

	public TestCreationProperties(String path) {
		propertyPath = path;
	}

	public void readProperties() throws IOException {
		File propertyFile = new File(propertyPath);
		reader = new BufferedReader(new FileReader(propertyFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.contains("projectPath")) {
				projectPath = line.substring(line.indexOf('=') + 1).trim().replace("\\", "\\\\");
			} else if (line.contains("xmlPath")) {
				xmlPath = line.substring(line.indexOf('=') + 1).trim().replace("\\", "\\\\");
			} else if (line.contains("testPackage")) {
				testPackage = line.substring(line.indexOf('=') + 1).trim();
			} else if (line.contains("compareClasses")) {
				compareClass = line.substring(line.indexOf('=') + 1).trim()
						.matches("true") ? true : false;
			} else if (line.contains("compareMethods")) {
				compareMethod = line.substring(line.indexOf('=') + 1).trim()
						.matches("true") ? true : false;
			}
		}
	}
}
