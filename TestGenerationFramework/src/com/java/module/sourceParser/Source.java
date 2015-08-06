package com.java.module.sourceParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Source {

	String sourceLocation;
	private Reader reader;
	Syntax syntax;

	public Source(String location) throws IOException {
		sourceLocation = location;
		reader = new Reader(location);
		syntax = new Syntax();

	}

	public ArrayList<String> getClasses() {
		return reader.getClasses();
	}

	public ArrayList<File> getClassFiles() {
		return reader.getClassFiles();
	}

	public Syntax getSyntax() throws IOException {
		reader.extractClassMethods();
//		reader.printClassMethods();
		syntax.classes = reader.getClassMethods();
		return syntax;
	}

}
