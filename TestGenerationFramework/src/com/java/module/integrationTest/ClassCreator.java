package com.java.module.integrationTest;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.java.module.testCaseStructure.IntegrationTest;

public class ClassCreator {

	IntegrationTest integrationTest;
	IntegrationTestCaseGenerator generator;

	public ClassCreator(IntegrationTest integrationTest,
			IntegrationTestCaseGenerator generator) {
		this.integrationTest = integrationTest;
		this.generator = generator;
	}

	public void createIntegrationTest() {
		CompilationUnit cu = createCU();
		try {
			File file = new File(integrationTest.testPath
					+ integrationTest.className + ".java");
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(cu.toString());
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private CompilationUnit createCU() {
		CompilationUnit cu = new CompilationUnit();
		cu.setPackage(new PackageDeclaration(ASTHelper
				.createNameExpr(integrationTest.packageName)));
		List<ImportDeclaration> imports = new ArrayList<>();
		for (int i = 0; i < integrationTest.imports.size(); i++) {
			imports.add(new ImportDeclaration(ASTHelper
					.createNameExpr(integrationTest.imports.get(i)), false,
					false));
		}
		cu.setImports(imports);
		ClassOrInterfaceDeclaration classDeclaration = new ClassOrInterfaceDeclaration(
				ModifierSet.PUBLIC, false, integrationTest.className);
		ASTHelper.addTypeDeclaration(cu, classDeclaration);
		for (int i = 0; i < integrationTest.listOfIntegratedClasses.size(); i++) {
			VariableDeclarator classVariableDeclaration = new VariableDeclarator(
					new VariableDeclaratorId(
							(char) (integrationTest.listOfIntegratedClasses
									.get(i).charAt(0) + 32)
									+ integrationTest.listOfIntegratedClasses
											.get(i).substring(1)));
			FieldDeclaration classVariableTypeDeclaration = new FieldDeclaration(
					ModifierSet.PRIVATE, ASTHelper.createReferenceType(
							integrationTest.listOfIntegratedClasses.get(i), 0),
					classVariableDeclaration);
			ASTHelper.addMember(classDeclaration, classVariableTypeDeclaration);
		}

		MethodDeclaration setUpMethod = new MethodDeclaration(
				ModifierSet.PUBLIC, ASTHelper.VOID_TYPE, "setUp");
		List<AnnotationExpr> beforeAnnotations = new ArrayList<AnnotationExpr>();
		AnnotationExpr annotation = new MarkerAnnotationExpr();
		annotation.setName(new NameExpr("Before"));
		beforeAnnotations.add(annotation);
		setUpMethod.setAnnotations(beforeAnnotations);
		ASTHelper.addMember(classDeclaration, setUpMethod);
		BlockStmt beforeBlock = new BlockStmt();
		for (int i = 0; i < integrationTest.listOfIntegratedClasses.size(); i++) {
			AssignExpr classInstantiationExpr = new AssignExpr(new NameExpr(
					(char) (integrationTest.listOfIntegratedClasses.get(i)
							.charAt(0) + 32)
							+ integrationTest.listOfIntegratedClasses.get(i)
									.substring(1)), new NameExpr(
					integrationTest.listOfIntegratedClassConstructors.get(i)),
					AssignExpr.Operator.assign);
			ASTHelper.addStmt(beforeBlock, classInstantiationExpr);
		}
		setUpMethod.setBody(beforeBlock);

		List<AnnotationExpr> testAnnotations = new ArrayList<AnnotationExpr>();
		AnnotationExpr expr = new MarkerAnnotationExpr();
		expr.setName(new NameExpr("Test"));
		testAnnotations.add(expr);
		MethodDeclaration method = new MethodDeclaration(ModifierSet.PUBLIC,
				ASTHelper.VOID_TYPE, integrationTest.testMethod.methodName);
		method.setAnnotations(testAnnotations);
		ASTHelper.addMember(classDeclaration, method);
		BlockStmt block = new BlockStmt();
		int varNo = 0;
		for (int i = 0; i < integrationTest.methodCallSequence.size(); i++) {
			NameExpr methodCallClass = new NameExpr(
					integrationTest.methodCallClassVar.get(i));
			MethodCallExpr methodCall = new MethodCallExpr(methodCallClass,
					integrationTest.methodCallSequence.get(i).methodName);
			if (integrationTest.methodCallSequence.get(i).parameters != null) {
				for (int j = 0; j < integrationTest.methodCallSequence.get(i).parameters
						.size(); j++) {
					if (integrationTest.methodCallSequence.get(i).parameters
							.get(j).getType().toString().matches("String")) {
						ASTHelper.addArgument(methodCall,
								new StringLiteralExpr("test"));
					} else if (integrationTest.methodCallSequence.get(i).parameters
							.get(j).getType().toString().matches("int")) {
						ASTHelper.addArgument(methodCall, new NameExpr("1"));
					} else if (integrationTest.methodCallSequence.get(i).parameters
							.get(j).getType().toString().contains("List<")) {
						ArrayList<VariableDeclarator> variables = new ArrayList<>();
						VariableDeclarator methodCallVariableDeclaration = new VariableDeclarator(
								new VariableDeclaratorId("var" + j));
						variables.add(methodCallVariableDeclaration);
						VariableDeclarationExpr methodCallVariableExpr = new VariableDeclarationExpr(
								ASTHelper.createReferenceType(
										integrationTest.methodCallSequence
												.get(i).parameters.get(j)
												.getType().toString(), 0),
								variables);
						ASTHelper.addStmt(block, methodCallVariableExpr);
						AssignExpr methodCallVariableAssign = new AssignExpr(
								new NameExpr("var" + varNo),
								new NameExpr("new "
										+ integrationTest.methodCallSequence
												.get(i).parameters.get(j)
												.getType().toString() + "()"),
								AssignExpr.Operator.assign);
						ASTHelper.addStmt(block, methodCallVariableAssign);
						ASTHelper.addArgument(methodCall, new NameExpr("var"
								+ varNo));
					} else {
						ArrayList<VariableDeclarator> variables = new ArrayList<>();
						VariableDeclarator methodCallVariableDeclaration = new VariableDeclarator(
								new VariableDeclaratorId("var" + varNo));
						variables.add(methodCallVariableDeclaration);
						VariableDeclarationExpr methodCallVariableExpr = new VariableDeclarationExpr(
								ASTHelper.createReferenceType(
										integrationTest.methodCallSequence
												.get(i).parameters.get(j)
												.getType().toString(), 0),
								variables);
						ASTHelper.addStmt(block, methodCallVariableExpr);
						AssignExpr methodCallVariableAssign = new AssignExpr(
								new NameExpr("var" + varNo),
								new NameExpr(
										generator
												.getClassInitialization(integrationTest.methodCallSequence
														.get(i).parameters
														.get(j).getType()
														.toString())),
								AssignExpr.Operator.assign);
						ASTHelper.addStmt(block, methodCallVariableAssign);
						ASTHelper.addArgument(methodCall, new NameExpr("var"
								+ varNo));
						varNo++;
					}

				}
			}
			ASTHelper.addStmt(block, methodCall);
		}
		method.setBody(block);
		return cu;
	}

}
