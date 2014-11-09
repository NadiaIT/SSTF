package com.java.module.sourceParser;

import japa.parser.ast.CompilationUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {
	private BufferedReader reader;
	File[] allFileList;
	ArrayList<File> javaFileList = new ArrayList<File>();
	ArrayList<String> listOfClasses = new ArrayList<String>();
	ArrayList<ClassInfo> classes;

	public Reader(String location) throws IOException {
		File folder = new File(location);
		File[] listOfFiles = folder.listFiles();
		getAllfiles(listOfFiles);

		for (int i = 0; i < javaFileList.size(); i++) {
			String fileName = javaFileList.get(i).getName();
			listOfClasses.add(fileName.substring(0, fileName.length() - 5));
			// readFile(javaFileList.get(i));
		}
	}

	public void getAllfiles(File[] listOfFiles) {
		for (File file : listOfFiles) {
			String fileName = file.getName();
			boolean isJavaCode = fileName.substring(
					fileName.lastIndexOf(".") + 1, fileName.length()).equals(
					"java");
			if (file.isFile()) {
				if (isJavaCode) {
					javaFileList.add(file);
				}
			} else {
				getAllfiles(file.listFiles());
			}
		}
	}

	public ArrayList<String> getClasses() {
		return listOfClasses;
	}

	public ArrayList<File> getClassFiles() {
		return javaFileList;
	}

	public ArrayList<ClassInfo> getClassMethods() {
		return classes;
	}

	public void readFile(File file) throws IOException {
		reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println();
	}

	public void extractClassMethods() throws IOException {
		JavaSourceParser javaSourceParser = new JavaSourceParser();
		MethodVisitor visitor = new MethodVisitor();
		CompilationUnit compilationUnit;

		classes = new ArrayList<ClassInfo>();

		for (int i = 0; i < javaFileList.size(); i++) {
			visitor.initializeMethodList();
			ClassInfo classinfo = new ClassInfo();
			classinfo.classNo = i;
			classinfo.className = javaFileList.get(i).getName()
					.substring(0, javaFileList.get(i).getName().length() - 5);

			compilationUnit = javaSourceParser.getCompilationunit(javaFileList
					.get(i));
			visitor.visit(compilationUnit, null);
			classinfo.methodDetails = visitor.getMethodList();
			classinfo.constructor = javaSourceParser.constructor;
			classinfo.packageName = javaSourceParser.packageDeclaration;
			classinfo.imports = javaSourceParser.importDeclarations;
			classinfo.isInterface = javaSourceParser.isInterface;
			classinfo.isAbstractClass=javaSourceParser.isAbstractClass;
			classes.add(classinfo);

		}
	}

	public void printClassMethods() {
		System.out.println("***Syntax Start***");
		for (int i = 0; i < classes.size(); i++) {
			System.out.print("Class ");
			System.out.print(classes.get(i).className);
			if (classes.get(i).constructor.parameters != null)
				System.out.print(" " + classes.get(i).constructor.parameters);
			System.out.println();
			if (classes.get(i).methodDetails.size() > 0)
				System.out.println("\tMethods:");
//			else System.out.println("\tNo Methods");
			for (int j = 0; j < classes.get(i).methodDetails.size(); j++) {
				System.out.println("\t\t"
						+ classes.get(i).methodDetails.get(j).methodName);
			}
		}
		System.out.println("***Syntax End***");
	}
}
