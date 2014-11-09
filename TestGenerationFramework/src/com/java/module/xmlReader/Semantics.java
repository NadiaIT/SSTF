package com.java.module.xmlReader;

import java.util.ArrayList;

import com.java.module.classDiagramAnalyzer.ClassDiagram;
import com.java.module.sequenceDiagramAnalyzer.SequenceDiagram;
import com.java.module.stateDiagramAnalyzer.StateDiagram;

public class Semantics {

	public ArrayList<ClassDiagram> classDiagram;
	public ArrayList<StateDiagram> stateDiagrams;
	public ArrayList<SequenceDiagram> sequenceDiagrams;

	public Semantics() {
		classDiagram = new ArrayList<ClassDiagram>();
		stateDiagrams = new ArrayList<StateDiagram>();
		sequenceDiagrams = new ArrayList<SequenceDiagram>();
	}
}
