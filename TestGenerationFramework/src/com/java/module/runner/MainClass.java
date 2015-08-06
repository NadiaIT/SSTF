package com.java.module.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.java.module.inconsistencyChecker.CompareSyntaxSemantics;
import com.java.module.inconsistencyChecker.ComparisonStatus;
import com.java.module.integrationTest.IntegrationTestCaseGenerator;
import com.java.module.sourceParser.Source;
import com.java.module.sourceParser.Syntax;
import com.java.module.unitTest.UnitTestCaseGenerator;
import com.java.module.xmlReader.Semantics;
import com.java.module.xmlReader.XMLReader;

public class MainClass {

	static ArrayList<String> classes;
	static ArrayList<File> classFiles;
	static Semantics semantics;
	static Syntax syntax;

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException {
		long start = System.currentTimeMillis();
		TestCreationProperties properties = new TestCreationProperties(
				"F:\\Codes\\ResearchCode_SSTF\\sample projects\\TestProperties_ATM.txt");
		properties.readProperties();
		File file = new File(properties.projectPath + "\\Details.txt");
		FileOutputStream fis = new FileOutputStream(file);
		PrintStream out = new PrintStream(fis);
		System.setOut(out);
		Source source = new Source(properties.projectPath + "\\src");
		classes = source.getClasses();
		classFiles = source.getClassFiles();
		syntax = source.getSyntax();
		XMLReader xmlReader = new XMLReader(properties.xmlPath);
		semantics = xmlReader.identifySemantics();
		if (properties.compareClass) {
			CompareSyntaxSemantics syntaxSemantics = new CompareSyntaxSemantics(
					semantics, syntax);
			ComparisonStatus status = syntaxSemantics
					.compareAndRemoveUnmatched(properties.compareMethod);
			Log.writeToFile(properties.projectPath + "\\ComparisonStatus.txt",
					status.printStatus(properties.compareMethod));
		}
		UnitTestCaseGenerator unitTestCaseGenerator = new UnitTestCaseGenerator(
				semantics, syntax, properties.projectPath + "\\test\\"
						+ properties.testPackage.replace(".", "\\\\") + "\\",
				properties.testPackage);
		String unitTestStatus = unitTestCaseGenerator.generateTests();
		IntegrationTestCaseGenerator integrationTestCaseGenerator = new IntegrationTestCaseGenerator(
				semantics, syntax, properties.projectPath + "\\test\\"
						+ properties.testPackage.replace(".", "\\\\") + "\\",
				properties.testPackage);
		String integrationTestStatus = integrationTestCaseGenerator
				.generateTests();
		long end = System.currentTimeMillis();
		String performance = "Time Elapsed = "
				+ (double) ((end - start) / 1000.0) + " seconds";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		performance += "\nDate = " + dateFormat.format(date);
		performance += "\nProject = "
				+ properties.projectPath.substring(properties.projectPath
						.lastIndexOf("\\") + 1);
		performance += "\n" + unitTestStatus;
		performance += "\n" + integrationTestStatus;
		Log.writeToFile(properties.projectPath + "\\PerformanceLog.txt",
				performance);
	}
}
