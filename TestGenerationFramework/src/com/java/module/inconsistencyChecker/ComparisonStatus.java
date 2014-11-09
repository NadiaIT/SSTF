package com.java.module.inconsistencyChecker;

import java.util.ArrayList;

public class ComparisonStatus {

	ArrayList<String> mismatchedSyntaxClasses;
	ArrayList<String> mismatchedSemanticsClasses;
	ArrayList<String> mismatchedSyntaxMethods;
	ArrayList<String> mismatchedSemanticsMethods;

	public ComparisonStatus() {
		mismatchedSemanticsClasses = new ArrayList<String>();
		mismatchedSyntaxClasses = new ArrayList<String>();
		mismatchedSemanticsMethods = new ArrayList<String>();
		mismatchedSyntaxMethods = new ArrayList<String>();
	}

	public String printStatus(boolean compareMethod) {
		String content = "";
		content += "Mismatched Semantics Classes:";
		for (int i = 0; i < mismatchedSemanticsClasses.size(); i++) {
			content += "\n\t" + mismatchedSemanticsClasses.get(i);
		}
		content += "\nMismatched Syntax Classes:";
		for (int i = 0; i < mismatchedSyntaxClasses.size(); i++) {
			content += "\n\t" + mismatchedSyntaxClasses.get(i);
		}
		if (compareMethod) {
			content += "\nMismatched Semantics Methods:";
			for (int i = 0; i < mismatchedSemanticsMethods.size(); i++) {
				content += "\n\t" + mismatchedSemanticsMethods.get(i);
			}
			content += "\nMismatched Syntax Methods:";
			for (int i = 0; i < mismatchedSyntaxMethods.size(); i++) {
				content += "\n\t" + mismatchedSyntaxMethods.get(i);
			}
		}
		return content;
	}
}
