package com.java.module.integrationTest;

import java.util.ArrayList;

import com.java.module.sourceParser.Syntax;
import com.java.module.testCaseStructure.IntegrationTest;
import com.java.module.testCaseStructure.TestMethod;
import com.java.module.xmlReader.Semantics;

public class IntegrationTestCaseGenerator {

	Semantics semantics;
	Syntax syntax;
	ArrayList<IntegrationTest> integrationTests;
	String path, packageName;

	public IntegrationTestCaseGenerator(Semantics semantics, Syntax syntax,
			String path, String packageName) {
		this.semantics = semantics;
		this.syntax = syntax;
		this.path = path;
		this.packageName = packageName;
		integrationTests = new ArrayList<IntegrationTest>();
	}

	public String generateTests() {
		String performanceString = "";
		String classesCreated = "";
		int classNo = 0;
		String testMethodsCreated = "";
		for (int i = 0; i < semantics.sequenceDiagrams.size(); i++) {
			IntegrationTest integrationTest = new IntegrationTest();
			integrationTest.className = semantics.sequenceDiagrams.get(i).label
					+ "Test";
			integrationTest.testPath = path;
			integrationTest.packageName = packageName;
			for (int j = 0; j < semantics.sequenceDiagrams.get(i).actors.size(); j++) {
				for (int k = 0; k < syntax.classes.size(); k++) {
					if (semantics.sequenceDiagrams.get(i).actors.get(j).label
							.matches(syntax.classes.get(k).className)) {
						integrationTest.imports
								.add(syntax.classes.get(k).packageName
										.getName().toString()
										+ "."
										+ syntax.classes.get(k).className);
						integrationTest.listOfIntegratedClasses
								.add(syntax.classes.get(k).className);
						String classConstructionString = getClassInitialization(syntax.classes
								.get(k).className);
						integrationTest.listOfIntegratedClassConstructors
								.add(classConstructionString);
					}
				}
			}
			integrationTest.imports.add("org.junit.Test");
			integrationTest.imports.add("org.junit.Before");
			integrationTest.imports.add("org.junit.Assert");
			// integrationTest.imports.add("java.util.ArrayList");
			// integrationTest.imports.add("java.util.List");
			integrationTest.imports.add("java.util.*");
			integrationTest.imports.add("java.awt.*");
			integrationTest.imports.add("java.awt.event.*");
			TestMethod testMethod = new TestMethod();
			testMethod.methodName = semantics.sequenceDiagrams.get(i).label
					+ "Test";
			integrationTest.testMethod = testMethod;
			for (int j = 0; j < semantics.sequenceDiagrams.get(i).events.size(); j++) {
				for (int k = 0; k < semantics.sequenceDiagrams.get(i).actors
						.size(); k++) {
					if (semantics.sequenceDiagrams.get(i).events.get(j).actorV == semantics.sequenceDiagrams
							.get(i).actors.get(k).actorNo) {
						for (int x = 0; x < syntax.classes.size(); x++) {
							if (syntax.classes.get(x).className
									.toLowerCase()
									.contains(
											semantics.sequenceDiagrams.get(i).actors
													.get(k).label.toLowerCase())) {
								for (int y = 0; y < syntax.classes.get(x).methodDetails
										.size(); y++) {
									if (syntax.classes.get(x).methodDetails
											.get(y).methodName.toLowerCase()
											.contains(
													semantics.sequenceDiagrams
															.get(i).events
															.get(j).label
															.toLowerCase())) {
										TestMethod method = new TestMethod();
										method.methodName = syntax.classes
												.get(x).methodDetails.get(y).methodName;
										method.parameters = syntax.classes
												.get(x).methodDetails.get(y).parameters;
										integrationTest.methodCallSequence
												.add(method);
										integrationTest.methodCallClassVar
												.add((char) (syntax.classes
														.get(x).className
														.charAt(0) + 32)
														+ syntax.classes.get(x).className
																.substring(1));
										break;
									}
								}
							}
						}
					}
				}
			}

			integrationTests.add(integrationTest);
			testMethodsCreated += "\n\t\t"
					+ integrationTest.testMethod.methodName;
			classesCreated += "\n\t\t" + integrationTest.className;
			classNo++;
			ClassCreator creator = new ClassCreator(integrationTest, this);
			creator.createIntegrationTest();
		}
		performanceString += "Integration Test:";
		performanceString += "\n\t" + classNo + " class(es) created";
		performanceString += classesCreated;
		performanceString += "\n\t" + classNo + " test method(s) created";
		performanceString += testMethodsCreated;
		return performanceString;
	}

	public String getClassInitialization(String className) {
		for (int i = 0; i < syntax.classes.size(); i++) {
			if (syntax.classes.get(i).className.matches(className)) {
				if (syntax.classes.get(i).isInterface
						|| syntax.classes.get(i).isAbstractClass)
					return "null";
				String classConstructionString = "new "
						+ syntax.classes.get(i).className + "(";
				int flag = 0;
				if (syntax.classes.get(i).constructor.parameters != null) {
					for (int j = 0; j < syntax.classes.get(i).constructor.parameters
							.size(); j++) {
						if (syntax.classes.get(i).constructor.parameters.get(j)
								.getType().toString().matches("String")) {
							if (flag == 1)
								classConstructionString += ",";
							flag = 1;
							classConstructionString += "\"test\"";
						} else if (syntax.classes.get(i).constructor.parameters
								.get(j).getType().toString().matches("int")) {
							if (flag == 1)
								classConstructionString += ",";
							flag = 1;
							classConstructionString += "1";
						} else if (syntax.classes.get(i).constructor.parameters
								.get(j).getType().toString().matches("double")) {
							if (flag == 1)
								classConstructionString += ",";
							flag = 1;
							classConstructionString += "1.0";
						} else {
							if (flag == 1)
								classConstructionString += ",";
							flag = 1;
							classConstructionString += getClassInitialization(syntax.classes
									.get(i).constructor.parameters.get(j)
									.getType().toString());
						}
					}
				}
				classConstructionString += ")";
				return classConstructionString;
			}
		}
		return "null";
	}
}
