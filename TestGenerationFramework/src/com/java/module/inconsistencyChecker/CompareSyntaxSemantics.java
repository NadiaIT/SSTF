package com.java.module.inconsistencyChecker;

import java.awt.Point;
import java.util.ArrayList;

import com.java.module.sourceParser.Syntax;
import com.java.module.xmlReader.Semantics;

public class CompareSyntaxSemantics {

	ComparisonStatus comparisonStatus;
	Semantics semantics;
	Syntax syntax;

	public CompareSyntaxSemantics(Semantics semantics, Syntax syntax) {
		this.semantics = semantics;
		this.syntax = syntax;
		comparisonStatus = new ComparisonStatus();
	}

	public ComparisonStatus compareAndRemoveUnmatched(boolean compareMethods) {
		int matchedClasses = 0;
		int matchedMethods = 0;
		ArrayList<Integer> toBeRemovedSyntaxIndexes = new ArrayList<>();
		ArrayList<Integer> toBeRemovedSemanticsIndexes = new ArrayList<>();
		ArrayList<Point> toBeRemovedSyntaxIndexMethods = new ArrayList<>();
		ArrayList<Point> toBeRemovedSemanticsIndexMethods = new ArrayList<>();
		for (int i = 0; i < syntax.classes.size(); i++) {
			matchedClasses = 0;
			for (int k = 0; k < semantics.classDiagram.size(); k++) {
				if (syntax.classes.get(i).className
						.trim()
						.toLowerCase()
						.matches(
								semantics.classDiagram.get(k).className.trim()
										.toLowerCase())) {
					matchedClasses++;
					if (compareMethods) {
						for (int j = 0; j < syntax.classes.get(i).methodDetails
								.size(); j++) {
							matchedMethods = 0;
							for (int l = 0; l < semantics.classDiagram.get(k).methods
									.size(); l++) {
								if (syntax.classes.get(i).methodDetails.get(j).methodName
										.trim()
										.toLowerCase()
										.matches(
												semantics.classDiagram.get(k).methods
														.get(l).trim()
														.toLowerCase())) {
									matchedMethods++;
									break;
								}
							}
							if (matchedMethods == 0) {
								comparisonStatus.mismatchedSyntaxMethods
										.add(syntax.classes.get(i).className
												+ ":"
												+ syntax.classes.get(i).methodDetails
														.get(j).methodName);
								// syntax.classes.get(i).methodDetails.remove(j);
								toBeRemovedSyntaxIndexMethods.add(new Point(i,
										j));
							}
						}
					}
				}
			}
			if (matchedClasses == 0) {
				comparisonStatus.mismatchedSyntaxClasses.add(syntax.classes
						.get(i).className);
				// syntax.classes.remove(i);
				toBeRemovedSyntaxIndexes.add(i);
			}
		}
		int[] methodIndex = new int[syntax.classes.size()];
		for (int j = 0; j < methodIndex.length; j++) {
			methodIndex[j] = 0;
		}
		for (int i = 0; i < toBeRemovedSyntaxIndexMethods.size(); i++) {
			Point point = toBeRemovedSyntaxIndexMethods.get(i);
			syntax.classes.get(point.x).methodDetails.remove(point.y
					+ methodIndex[point.x]);
			methodIndex[point.x]--;
		}
		int index = 0;
		for (int i = 0; i < toBeRemovedSyntaxIndexes.size(); i++) {
			syntax.classes.remove(toBeRemovedSyntaxIndexes.get(i) + index);
			index--;
		}
		for (int i = 0; i < semantics.classDiagram.size(); i++) {
			matchedClasses = 0;
			for (int k = 0; k < syntax.classes.size(); k++) {
				if (semantics.classDiagram.get(i).className
						.trim()
						.toLowerCase()
						.matches(
								syntax.classes.get(k).className.trim()
										.toLowerCase())) {
					matchedClasses++;
					if (compareMethods) {
						for (int j = 0; j < semantics.classDiagram.get(i).methods
								.size(); j++) {
							matchedMethods = 0;
							toBeRemovedSemanticsIndexMethods.clear();
							for (int l = 0; l < syntax.classes.get(k).methodDetails
									.size(); l++) {
								if (semantics.classDiagram.get(i).methods
										.get(j)
										.trim()
										.toLowerCase()
										.matches(
												syntax.classes.get(k).methodDetails
														.get(l).methodName
														.trim().toLowerCase())) {
									matchedMethods++;
									break;
								}
							}
							if (matchedMethods == 0) {
								comparisonStatus.mismatchedSemanticsMethods
										.add(semantics.classDiagram.get(i).className
												+ ":"
												+ semantics.classDiagram.get(i).methods
														.get(j));
								// semantics.classDiagram.get(i).methods.remove(j);

								toBeRemovedSemanticsIndexMethods.add(new Point(
										i, j));
							}
						}

					}
				}
			}
			if (matchedClasses == 0) {
				comparisonStatus.mismatchedSemanticsClasses
						.add(semantics.classDiagram.get(i).className);
				// semantics.classDiagram.remove(i);
				toBeRemovedSemanticsIndexes.add(i);
			}
		}
		methodIndex = new int[semantics.classDiagram.size()];
		for (int j = 0; j < methodIndex.length; j++) {
			methodIndex[j] = 0;
		}
		for (int i = 0; i < toBeRemovedSemanticsIndexMethods.size(); i++) {
			Point point = toBeRemovedSemanticsIndexMethods.get(i);
			semantics.classDiagram.get(point.x).methods.remove(point.y
					+ methodIndex[point.x]);
			methodIndex[point.x]--;
		}
		index = 0;
		for (int i = 0; i < toBeRemovedSemanticsIndexes.size(); i++) {
			semantics.classDiagram.remove(toBeRemovedSemanticsIndexes.get(i)
					+ index);
			index--;
		}

		return comparisonStatus;
	}
}