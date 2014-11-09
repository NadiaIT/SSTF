package com.java.module.xmlReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class XMLReader {

	File classXML;
	ArrayList<File> stateXMLs = new ArrayList<File>();
	ArrayList<File> sequenceXMLs = new ArrayList<File>();
	SemanticsIdentifier semanticsIdentifier;

	public XMLReader(String path) throws IOException,
			ParserConfigurationException, SAXException {
		File folder = new File(path);
		categorizeFilesFolderwise(folder.listFiles());
	}

	public Semantics identifySemantics() throws IOException,
			ParserConfigurationException, SAXException {
		semanticsIdentifier = new SemanticsIdentifier(classXML, stateXMLs,
				sequenceXMLs);
		return semanticsIdentifier.identifySemantics();
		// System.out.println(classXML.getName());
		// for (int i = 0; i < stateXMLs.size(); i++) {
		// System.out.println(stateXMLs.get(i).getName());
		// }
		// for (int i = 0; i < sequenceXMLs.size(); i++) {
		// System.out.println(sequenceXMLs.get(i).getName());
		// }
	}

	public void categorizeFilesFolderwise(File[] listOfFiles) {
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.isDirectory()) {
					if (file.getName().contains("Class")) {
						if (file.listFiles().length > 0)
							classXML = file.listFiles()[0];
					} else if (file.getName().contains("State")) {
						getAllfiles(file.listFiles(), "State");
					} else if (file.getName().contains("Sequence")) {
						getAllfiles(file.listFiles(), "Sequence");
					}
				}
			}
		}
	}

	public void getAllfiles(File[] listOfFiles, String xmlType) {
		for (File file : listOfFiles) {
			String fileName = file.getName();
			boolean isXML = fileName.substring(fileName.lastIndexOf(".") + 1,
					fileName.length()).equals("xml");
			if (file.isFile() && isXML) {
				if (xmlType.matches("State")) {
					stateXMLs.add(file);
				} else {
					sequenceXMLs.add(file);
				}
			} else {
				getAllfiles(file.listFiles(), xmlType);
			}
		}
	}
}
