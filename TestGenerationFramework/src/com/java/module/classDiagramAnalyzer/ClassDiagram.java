package com.java.module.classDiagramAnalyzer;

import java.util.ArrayList;

public class ClassDiagram {

	public String className;
	public ArrayList<String> variables;
	public ArrayList<String> methods;

	public ClassDiagram() {
		variables = new ArrayList<String>();
		methods = new ArrayList<String>();
	}

}
