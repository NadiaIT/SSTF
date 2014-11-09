package com.java.module.unitTest;

import java.util.ArrayList;

import com.java.module.sourceParser.Syntax;
import com.java.module.testCaseStructure.TestMethod;
import com.java.module.testCaseStructure.UnitTest;
import com.java.module.xmlReader.Semantics;

public class UnitTestCaseGenerator {

	Semantics semantics;
	Syntax syntax;
	ArrayList<UnitTest> unitTests;
	String path, packageName;

	public UnitTestCaseGenerator(Semantics semantics, Syntax syntax,
			String path, String packageName) {
		this.semantics = semantics;
		this.syntax = syntax;
		this.path = path;
		this.packageName = packageName;
		unitTests = new ArrayList<UnitTest>();
	}

	public String generateTests() {
		String performanceString = "";
		String classesCreated = "";
		int classNo = 0;
		String testMethodsCreated = "";
		int testMethodNo = 0;
		for (int i = 0; i < syntax.classes.size(); i++) {
			if (syntax.classes.get(i).isInterface
					|| syntax.classes.get(i).isAbstractClass)
				continue;
			UnitTest unitTest = new UnitTest();
			unitTest.className = syntax.classes.get(i).className + "Test";
			unitTest.packageName = packageName;
			unitTest.testPath = path;
			for (int j = 0; j < syntax.classes.size(); j++) {
				int l;
				for (l = 0; l < unitTest.imports.size(); l++) {
					if ((syntax.classes.get(j).packageName.getName().toString() + ".*")
							.matches(unitTest.imports.get(l))) {
						break;
					}
				}
				if (unitTest.imports.size() == l) {
					unitTest.imports.add(syntax.classes.get(j).packageName
							.getName().toString() + ".*");
				}

			}
			unitTest.imports.add("org.junit.Test");
			unitTest.imports.add("org.junit.Before");
			unitTest.imports.add("org.junit.Assert");
//			unitTest.imports.add("java.util.ArrayList");
//			unitTest.imports.add("java.util.List");
			unitTest.imports.add("java.util.*");
			unitTest.imports.add("java.awt.*");
			unitTest.imports.add("java.awt.event.*");
			unitTest.imports.add("org.easymock.EasyMock");
			unitTest.classConstructionString = getClassInitialization(syntax.classes
					.get(i).className);

			for (int j = 0; j < syntax.classes.get(i).methodDetails.size(); j++) {
				// if (!syntax.classes.get(i).methodDetails.get(j).methodName
				// .matches("main")) {
				TestMethod method = new TestMethod();
				method.methodName = syntax.classes.get(i).methodDetails.get(j).methodName
						+ "Test";
				method.parameters = syntax.classes.get(i).methodDetails.get(j).parameters;
				unitTest.testMethods.add(method);
				testMethodNo++;
				testMethodsCreated += "\n\t\t" + method.methodName;
				// }
			}
			unitTests.add(unitTest);
			ClassCreator creator = new ClassCreator(unitTest);
			creator.createUnitTest();
			classNo++;
			classesCreated += "\n\t\t" + unitTest.className;
		}
		performanceString += "Unit Test:";
		performanceString += "\n\t" + classNo + " class(es) created";
		performanceString += classesCreated;
		performanceString += "\n\t" + testMethodNo + " test method(s) created";
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
