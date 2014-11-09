package com.java.module.sequenceDiagramAnalyzer;

import java.util.ArrayList;

public class SequenceDiagram {
	public String label;
	public int no;
	public ArrayList<Actor> actors;
	public ArrayList<Event> events;

	public SequenceDiagram() {
		actors = new ArrayList<Actor>();
		events = new ArrayList<Event>();
	}
	

}