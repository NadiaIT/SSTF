package com.java.module.stateDiagramAnalyzer;

import java.util.ArrayList;

public class StateDiagram {
	String className;
	ArrayList<State> states;
	ArrayList<Connector> connectors;

	public StateDiagram() {
		states = new ArrayList<State>();
		connectors = new ArrayList<Connector>();
	}
}
