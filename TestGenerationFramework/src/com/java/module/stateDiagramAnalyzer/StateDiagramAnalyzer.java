package com.java.module.stateDiagramAnalyzer;

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

public class StateDiagramAnalyzer {

	ArrayList<StateDiagram> stateDiagrams;
	ArrayList<File> stateXMLs;
	Document doc;

	public StateDiagramAnalyzer(ArrayList<File> stateXMLs) {
		stateDiagrams = new ArrayList<StateDiagram>();
		this.stateXMLs = stateXMLs;
	}

	public ArrayList<StateDiagram> generateStateDiagrams()
			throws ParserConfigurationException, SAXException, IOException {
		for (int i = 0; i < stateXMLs.size(); i++) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(stateXMLs.get(i));
			doc.getDocumentElement().normalize();
			StateDiagram stateDiagram = new StateDiagram();
			stateDiagram.className = stateXMLs.get(i).getName()
					.substring(0, stateXMLs.get(i).getName().length() - 4);
			Node parentNode = doc.getElementsByTagName("elements").item(0);
			stateDiagram.states = identifyStateElements(parentNode);
			parentNode = doc.getElementsByTagName("connectors").item(0);
			stateDiagram.connectors = identifyStateConnectors(parentNode,
					stateDiagram.states);
			stateDiagrams.add(stateDiagram);
		}
		return stateDiagrams;
	}

	private ArrayList<State> identifyStateElements(Node parentNode) {
		ArrayList<State> states = new ArrayList<State>();
		int count = 0;
		NodeList childs = parentNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) childs.item(i);
				if (eElement.getAttribute("xmi:type").matches("uml:State")) {
					State state = new State();
					state.label = eElement.getAttribute("name");
					state.stateNo = count++;
					states.add(state);
					// System.out.println("state: "
					// + eElement.getAttribute("name"));
				}
			}
		}
		return states;
	}

	private ArrayList<Connector> identifyStateConnectors(Node parentNode,
			ArrayList<State> states) {
		ArrayList<Connector> connectors = new ArrayList<>();
		NodeList childs = parentNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Connector connector = new Connector();
				NodeList nodes = childs.item(i).getChildNodes();
				for (int j = 0; j < nodes.getLength(); j++) {
					if (nodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if (nodes.item(j).getNodeName() == "source") {
							connector.nodeU = findConnectedElements(
									nodes.item(j), states);
						} else if (nodes.item(j).getNodeName() == "target") {
							connector.nodeV = findConnectedElements(
									nodes.item(j), states);
						}
					}
				}
				connectors.add(connector);
			}
		}
		NodeList labelNode = doc.getElementsByTagName("transition");
		for (int i = 0; i < labelNode.getLength(); i++) {
			Element label = (Element) labelNode.item(i);
			connectors.get(i).label = label.getAttribute("name");
			// System.out.println(label.getAttribute("name"));
		}
		return connectors;
	}

	private int findConnectedElements(Node node, ArrayList<State> states) {
		NodeList nodeProperties = node.getChildNodes();
		for (int i = 0; i < nodeProperties.getLength(); i++) {
			if (nodeProperties.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nodeProperties.item(i);
				if (eElement.getNodeName() == "model"
						&& eElement.getAttribute("type").matches("State")) {
					for (int j = 0; j < states.size(); j++) {
						// System.out.println(eElement.getAttribute("name") +
						// "-"
						// + states.get(j).label);
						if (states.get(j).label.matches(eElement
								.getAttribute("name"))) {
							// System.out.print(eElement.getAttribute("name"));
							return j;
						}
					}
				}
			}
		}
		return 0;
	}

	public void printStateDiagrams() {
		System.out.println("**State Diagrams**");
		for (int i = 0; i < stateDiagrams.size(); i++) {
			System.out.println(stateDiagrams.get(i).className);
			System.out.print("Nodes: ");
			for (int j = 0; j < stateDiagrams.get(i).states.size(); j++) {
				System.out.print("|" + stateDiagrams.get(i).states.get(j).label
						+ "| ");
			}
			for (int k = 0; k < stateDiagrams.get(i).connectors.size(); k++) {
				System.out.println();
				for (int j = 0; j < stateDiagrams.get(i).states.size(); j++) {
					if (stateDiagrams.get(i).states.get(j).stateNo == stateDiagrams
							.get(i).connectors.get(k).nodeU) {
						System.out.print(" s:"
								+ stateDiagrams.get(i).states.get(j).label);
						System.out.print(" <-"
								+ stateDiagrams.get(i).connectors.get(k).label
								+ "->");
					}
					if (stateDiagrams.get(i).states.get(j).stateNo == stateDiagrams
							.get(i).connectors.get(k).nodeV) {
						System.out.print(" d:"
								+ stateDiagrams.get(i).states.get(j).label);
					}
				}
			}
		}
		System.out.println();
	}
}
