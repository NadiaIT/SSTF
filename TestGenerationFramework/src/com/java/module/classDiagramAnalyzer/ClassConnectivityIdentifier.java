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

public class ClassConnectivityIdentifier {

	int matrix[][];
	ArrayList<String> classNames;
	File classXML;

	public ClassConnectivityIdentifier(int classNumber, File classXML,
			ArrayList<String> classNames) throws IOException,
			ParserConfigurationException, SAXException {
		matrix = new int[classNumber][classNumber];
		this.classXML = classXML;
		this.classNames = classNames;
		for (int i = 0; i < classNumber; i++) {
			for (int j = 0; j < classNumber; j++) {
				matrix[i][j] = 0;
				matrix[j][i] = 0;
			}
		}
	}

	public void parseClassConnectivity() throws IOException,
			ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		if (classXML != null) {
			Document doc = dBuilder.parse(classXML);
			doc.getDocumentElement().normalize();
			Node parentNode = doc.getElementsByTagName("connectors").item(0);
			identifyClassConnectors(parentNode);
		}
	}

	private void identifyClassConnectors(Node parentNode) {
		NodeList childs = parentNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				NodeList nodes = childs.item(i).getChildNodes();
				int source = -1, target = -1;
				for (int j = 0; j < nodes.getLength(); j++) {
					if (nodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if (nodes.item(j).getNodeName() == "source") {
							source = findConnectedElements(nodes.item(j));
						} else if (nodes.item(j).getNodeName() == "target") {
							target = findConnectedElements(nodes.item(j));
						}
					}
				}
				if (source != -1 && target != -1) {
					matrix[source][target]++;
				}
			}
		}
	}

	private int findConnectedElements(Node node) {
		NodeList nodeProperties = node.getChildNodes();
		for (int i = 0; i < nodeProperties.getLength(); i++) {
			if (nodeProperties.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nodeProperties.item(i);
				if (eElement.getNodeName() == "model"
						&& (eElement.getAttribute("type").matches("Class"))) {
					for (int j = 0; j < classNames.size(); j++) {
						if (classNames.get(j).matches(
								eElement.getAttribute("name"))) {
							return j;
						}
					}
				}
			}
		}
		return -1;
	}

	public void printMatrix() {
		System.out.println("***Matrix***");
		for (int i = 0; i < classNames.size(); i++) {
			System.out.print(classNames.get(i) + " ");
		}
		System.out.println();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
}
