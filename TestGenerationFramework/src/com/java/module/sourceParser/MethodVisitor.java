package com.java.module.sourceParser;

import java.util.ArrayList;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class MethodVisitor extends VoidVisitorAdapter<Object> {

	ArrayList<Method> methodList;

	public void visit(MethodDeclaration n, Object arg) {

		// System.out.print(n.getModifiers());
		// System.out.println(" " + n.getName());
		if(n.getModifiers()!=1) return;
		Method method = new Method();
		method.methodBody = n.getBody();
		method.methodName = n.getName();
		method.annotation = n.getAnnotations();
		method.compilationUnit = n;
		method.parameters = n.getParameters();
		method.startingLine = n.getBeginLine();
		method.endingLine = n.getEndLine();
		method.throwed = n.getThrows();

		methodList.add(method);
	}

	public ArrayList<Method> getMethodList() {
		return methodList;
	}

	public void initializeMethodList() {
		methodList = new ArrayList<Method>();
	}
}
