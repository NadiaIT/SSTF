package com.java.module.testCaseStructure;

import java.util.ArrayList;

public class IntegrationTest extends TestCase {

	public ArrayList<String> listOfIntegratedClasses;
	public ArrayList<String> listOfIntegratedClassConstructors;
	public ArrayList<TestMethod> methodCallSequence;
	public ArrayList<String> methodCallClassVar;
	public TestMethod testMethod;

	public IntegrationTest() {
		super();
		listOfIntegratedClasses = new ArrayList<String>();
		listOfIntegratedClassConstructors = new ArrayList<String>();
		methodCallSequence = new ArrayList<TestMethod>();
		methodCallClassVar = new ArrayList<String>();
	}
}
