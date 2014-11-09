package com.java.module.testCaseStructure;

import java.util.ArrayList;

public abstract class TestCase {

	public String testPath;
	public String packageName;
	public ArrayList<String> imports;
	public String className;
	
	public TestCase() {
		imports = new ArrayList<String>();
	}
}
