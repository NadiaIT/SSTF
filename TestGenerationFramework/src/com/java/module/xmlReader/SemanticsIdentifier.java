package com.java.module.xmlReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.java.module.classDiagramAnalyzer.ClassDiagramAnalyzer;
import com.java.module.sequenceDiagramAnalyzer.SequenceDiagramAnalyzer;
import com.java.module.stateDiagramAnalyzer.StateDiagramAnalyzer;

public class SemanticsIdentifier {

	File classXML;
	ArrayList<File> stateXMLs;
	ArrayList<File> sequenceXMLs;
	ClassDiagramAnalyzer classDiagramAnalyzer;
	StateDiagramAnalyzer stateDiagramAnalyzer;
	SequenceDiagramAnalyzer sequenceDiagramAnalyzer;
	Semantics semantics;

	public SemanticsIdentifier(File classXML, ArrayList<File> stateXMLs,
			ArrayList<File> sequenceXMLs) throws IOException,
			ParserConfigurationException, SAXException {
		this.classXML = classXML;
		this.stateXMLs = stateXMLs;
		this.sequenceXMLs = sequenceXMLs;
		classDiagramAnalyzer = new ClassDiagramAnalyzer(classXML);
		stateDiagramAnalyzer = new StateDiagramAnalyzer(stateXMLs);
		sequenceDiagramAnalyzer = new SequenceDiagramAnalyzer(sequenceXMLs);
		semantics = new Semantics();
	}

	public Semantics identifySemantics() throws IOException,
			ParserConfigurationException, SAXException {
		System.out.println("***Semantics Start***");
		semantics.classDiagram = classDiagramAnalyzer.generateClassDiagrams();
		classDiagramAnalyzer.printClassDiagrams();
		semantics.stateDiagrams = stateDiagramAnalyzer.generateStateDiagrams();
		stateDiagramAnalyzer.printStateDiagrams();
		semantics.sequenceDiagrams = sequenceDiagramAnalyzer
				.generateSequenceDiagrams();
		sequenceDiagramAnalyzer.printSequenceDiagrams();
		System.out.println("***Semantics End***");
		return semantics;
	}

}
