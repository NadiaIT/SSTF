package com.java.module.testCaseStructure;

import java.util.ArrayList;

public class UnitTest extends TestCase {
	public String classConstructionString;
	public ArrayList<TestMethod> testMethods;

	public UnitTest() {
		super();
		testMethods = new ArrayList<TestMethod>();
	}
}
