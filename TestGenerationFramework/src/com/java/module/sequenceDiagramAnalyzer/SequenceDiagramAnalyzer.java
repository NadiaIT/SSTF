package com.java.module.sequenceDiagramAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SequenceDiagramAnalyzer {
	ArrayList<SequenceDiagram> sequenceDiagrams;
	ArrayList<File> sequenceXMLs;
	Document doc;

	public SequenceDiagramAnalyzer(ArrayList<File> sequenceXMLs) {
		sequenceDiagrams = new ArrayList<SequenceDiagram>();
		this.sequenceXMLs = sequenceXMLs;
	}

	public ArrayList<SequenceDiagram> generateSequenceDiagrams()
			throws ParserConfigurationException, SAXException, IOException {
		for (int i = 0; i < sequenceXMLs.size(); i++) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(sequenceXMLs.get(i));
			doc.getDocumentElement().normalize();
			SequenceDiagram sequenceDiagram = new SequenceDiagram();
			sequenceDiagram.label = sequenceXMLs.get(i).getName()
					.substring(0, sequenceXMLs.get(i).getName().length() - 4);
			sequenceDiagram.no = i + 1;
			Node parentNode = doc.getElementsByTagName("elements").item(0);
			sequenceDiagram.actors = identifySequenceElements(parentNode);
			parentNode = doc.getElementsByTagName("connectors").item(0);
			sequenceDiagram.events = identifySequenceConnectors(parentNode,
					sequenceDiagram.actors);
			sequenceDiagrams.add(sequenceDiagram);
		}
		return sequenceDiagrams;
	}

	private ArrayList<Actor> identifySequenceElements(Node parentNode) {
		ArrayList<Actor> actors = new ArrayList<Actor>();
		int count = 0;
		NodeList childs = parentNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) childs.item(i);
				if (eElement.getAttribute("xmi:type").matches("uml:Sequence")) {
					Actor actor = new Actor();
					actor.label = eElement.getAttribute("name");
					actor.actorNo = count++;
					actors.add(actor);
					// System.out.println("actor: "
					// + eElement.getAttribute("name"));
				}
			}
		}
		return actors;
	}

	private ArrayList<Event> identifySequenceConnectors(Node parentNode,
			ArrayList<Actor> actors) {
		ArrayList<Event> events = new ArrayList<>();
		NodeList childs = parentNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Event event = new Event();
				NodeList nodes = childs.item(i).getChildNodes();
				for (int j = 0; j < nodes.getLength(); j++) {
					if (nodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if (nodes.item(j).getNodeName() == "source") {
							event.actorU = findConnectedElements(nodes.item(j),
									actors);
						} else if (nodes.item(j).getNodeName() == "target") {
							event.actorV = findConnectedElements(nodes.item(j),
									actors);
						} else if (nodes.item(j).getNodeName() == "properties") {
							Element eElement = (Element) nodes.item(j);
							event.label = eElement.getAttribute("name");
							// System.out.print(" " + event.label);
						}
					}
				}
			//	System.out.println();
				events.add(event);
			}
		}
		return events;
	}

	private int findConnectedElements(Node node, ArrayList<Actor> actors) {
		NodeList nodeProperties = node.getChildNodes();
		for (int i = 0; i < nodeProperties.getLength(); i++) {
			if (nodeProperties.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nodeProperties.item(i);
				if (eElement.getNodeName() == "model"
						&& eElement.getAttribute("type").matches("Sequence")) {
					for (int j = 0; j < actors.size(); j++) {
						// System.out.println(eElement.getAttribute("name") +
						// "-"
						// + actors.get(j).label);
						if (actors.get(j).label.matches(eElement
								.getAttribute("name"))) {
							// System.out.print(eElement.getAttribute("name")
							// + " ");
							return j;
						}
					}
				}
			}
		}
		return 0;
	}

	public void printSequenceDiagrams() {
		System.out.println("**Sequence Diagrams**");
		for (int i = 0; i < sequenceDiagrams.size(); i++) {
			System.out.println(sequenceDiagrams.get(i).label);
			System.out.print("Actors: ");
			for (int j = 0; j < sequenceDiagrams.get(i).actors.size(); j++) {
				System.out.print("|"
						+ sequenceDiagrams.get(i).actors.get(j).label + "| ");
			}
			for (int k = 0; k < sequenceDiagrams.get(i).events.size(); k++) {
				System.out.println();
				for (int j = 0; j < sequenceDiagrams.get(i).actors.size(); j++) {
					if (sequenceDiagrams.get(i).actors.get(j).actorNo == sequenceDiagrams
							.get(i).events.get(k).actorU) {
						System.out.print(" s:"
								+ sequenceDiagrams.get(i).actors.get(j).label);
						System.out.print(" <-"
								+ sequenceDiagrams.get(i).events.get(k).label
								+ "->");
					}
					if (sequenceDiagrams.get(i).actors.get(j).actorNo == sequenceDiagrams
							.get(i).events.get(k).actorV) {
						System.out.print(" d:"
								+ sequenceDiagrams.get(i).actors.get(j).label);
					}
				}
			}
		}
		System.out.println();
	}
}
