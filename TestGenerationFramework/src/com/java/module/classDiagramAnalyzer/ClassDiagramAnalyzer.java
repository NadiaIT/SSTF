package com.java.module.classDiagramAnalyzer;

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

public class ClassDiagramAnalyzer {

	ArrayList<ClassDiagram> classDiagrams;
	File classXML;

	public ClassDiagramAnalyzer(File classXML) throws IOException,
			ParserConfigurationException, SAXException {
		classDiagrams = new ArrayList<ClassDiagram>();
		this.classXML = classXML;
	}

	public ArrayList<ClassDiagram> generateClassDiagrams() throws IOException,
			ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		if (classXML != null) {
			Document doc = dBuilder.parse(classXML);
			doc.getDocumentElement().normalize();
			Node parentNode = doc.getElementsByTagName("elements").item(0);
			identifyClassElements(parentNode);
		}
		return classDiagrams;
	}

	private void identifyClassElements(Node parentNode) {
		NodeList childs = parentNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) childs.item(i);
				if (eElement.getAttribute("xmi:type").matches("uml:Class")) {
					// || eElement.getAttribute("xmi:type").matches(
					// "uml:Interface")
					// System.out.println("class: "
					// + eElement.getAttribute("name"));
					ClassDiagram classDiagram = new ClassDiagram();
					classDiagram.className = (eElement.getAttribute("name"));
					classDiagram.variables = getClassProperties(eElement,
							"attributes");
					classDiagram.methods = getClassProperties(eElement,
							"operations");
					classDiagrams.add(classDiagram);
				}
			}
		}
	}

	private ArrayList<String> getClassProperties(Node parentNode,
			String property) {
		ArrayList<String> properties = new ArrayList<>();
		NodeList propertyNodes = null;
		NodeList childs = parentNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE
					&& childs.item(i).getNodeName() == property) {
				propertyNodes = childs.item(i).getChildNodes();
				break;
			}
		}
		if (propertyNodes != null) {
			for (int i = 0; i < propertyNodes.getLength(); i++) {
				if (propertyNodes.item(i).getNodeType() == Node.ELEMENT_NODE
						&& !propertyNodes.item(i).getNodeName()
								.matches("#comment")) {
					Element eElement = (Element) propertyNodes.item(i);
					properties.add(eElement.getAttribute("name"));
					// System.out.println(property + ": "
					// + eElement.getAttribute("name"));
				}
			}
		}
		return properties;
	}

	public void printClassDiagrams() {
		System.out.println("**Class Diagrams**");
		for (int i = 0; i < classDiagrams.size(); i++) {
			System.out.println("Class " + classDiagrams.get(i).className);
			if (classDiagrams.get(i).variables.size() > 0)
				System.out.println("\tVariables:");
			for (int j = 0; j < classDiagrams.get(i).variables.size(); j++) {
				System.out.println("\t\t"
						+ classDiagrams.get(i).variables.get(j));
			}
			if (classDiagrams.get(i).methods.size() > 0)
				System.out.println("\tMethods:");
			for (int j = 0; j < classDiagrams.get(i).methods.size(); j++) {
				System.out
						.println("\t\t" + classDiagrams.get(i).methods.get(j));
			}
		}
	}

}
