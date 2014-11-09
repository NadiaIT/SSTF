package com.java.module.unitTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.java.module.testCaseStructure.UnitTest;

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

public class ClassCreator {

	UnitTest unitTest;

	public ClassCreator(UnitTest unitTest) {
		this.unitTest = unitTest;
	}

	public void createUnitTest() {
		CompilationUnit cu = createCU();
		try {
			File file = new File(unitTest.testPath + unitTest.className
					+ ".java");
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
				.createNameExpr(unitTest.packageName)));
		List<ImportDeclaration> imports = new ArrayList<>();
		for (int i = 0; i < unitTest.imports.size(); i++) {
			imports.add(new ImportDeclaration(ASTHelper
					.createNameExpr(unitTest.imports.get(i)), false, false));
		}
		cu.setImports(imports);
		ClassOrInterfaceDeclaration classDeclaration = new ClassOrInterfaceDeclaration(
				ModifierSet.PUBLIC, false, unitTest.className);
		ASTHelper.addTypeDeclaration(cu, classDeclaration);
		String classVariableString = (char) (unitTest.className.charAt(0) + 32)
				+ unitTest.className.substring(1,
						unitTest.className.length() - 4);
		VariableDeclarator classVariableDeclaration = new VariableDeclarator(
				new VariableDeclaratorId(classVariableString));
		FieldDeclaration classVariableTypeDeclaration = new FieldDeclaration(
				ModifierSet.PRIVATE, ASTHelper.createReferenceType(
						unitTest.className.substring(0,
								unitTest.className.length() - 4), 0),
				classVariableDeclaration);
		ASTHelper.addMember(classDeclaration, classVariableTypeDeclaration);

		MethodDeclaration setUpMethod = new MethodDeclaration(
				ModifierSet.PUBLIC, ASTHelper.VOID_TYPE, "setUp");
		List<AnnotationExpr> beforeAnnotations = new ArrayList<AnnotationExpr>();
		AnnotationExpr annotation = new MarkerAnnotationExpr();
		annotation.setName(new NameExpr("Before"));
		beforeAnnotations.add(annotation);
		setUpMethod.setAnnotations(beforeAnnotations);
		ASTHelper.addMember(classDeclaration, setUpMethod);
		BlockStmt beforeBlock = new BlockStmt();
		AssignExpr classInstantiationExpr = new AssignExpr(new NameExpr(
				classVariableString), new NameExpr(
				unitTest.classConstructionString), AssignExpr.Operator.assign);
		ASTHelper.addStmt(beforeBlock, classInstantiationExpr);
		setUpMethod.setBody(beforeBlock);

		List<AnnotationExpr> testAnnotations = new ArrayList<AnnotationExpr>();
		AnnotationExpr expr = new MarkerAnnotationExpr();
		expr.setName(new NameExpr("Test"));
		testAnnotations.add(expr);
		for (int i = 0; i < unitTest.testMethods.size(); i++) {
			MethodDeclaration method = new MethodDeclaration(
					ModifierSet.PUBLIC, ASTHelper.VOID_TYPE,
					unitTest.testMethods.get(i).methodName + i);
			method.setAnnotations(testAnnotations);
			ASTHelper.addMember(classDeclaration, method);
			BlockStmt block = new BlockStmt();
			NameExpr classNameExpr = new NameExpr(classVariableString);
			MethodCallExpr methodCallExpr = new MethodCallExpr(classNameExpr,
					unitTest.testMethods.get(i).methodName
							.substring(0,
									unitTest.testMethods.get(i).methodName
											.length() - 4));
			if (unitTest.testMethods.get(i).parameters != null) {
				for (int j = 0; j < unitTest.testMethods.get(i).parameters
						.size(); j++) {
					// System.out.println(unitTest.testMethods.get(i).parameters
					// .get(j).getType());
					if (unitTest.testMethods.get(i).parameters.get(j).getType()
							.toString().matches("String")) {
						ASTHelper.addArgument(methodCallExpr,
								new StringLiteralExpr("test"));
					} else if (unitTest.testMethods.get(i).parameters.get(j)
							.getType().toString().matches("int")) {
						ASTHelper
								.addArgument(methodCallExpr, new NameExpr("1"));
					} else if (unitTest.testMethods.get(i).parameters.get(j)
							.getType().toString().contains("List<")) {
						ArrayList<VariableDeclarator> variables = new ArrayList<>();
						VariableDeclarator methodCallVariableDeclaration = new VariableDeclarator(
								new VariableDeclaratorId("var" + j));
						variables.add(methodCallVariableDeclaration);
						VariableDeclarationExpr methodCallVariableExpr = new VariableDeclarationExpr(
								ASTHelper
										.createReferenceType(
												unitTest.testMethods.get(i).parameters
														.get(j).getType()
														.toString(), 0),
								variables);
						ASTHelper.addStmt(block, methodCallVariableExpr);
						AssignExpr methodCallVariableAssign = new AssignExpr(
								new NameExpr("var" + j),
								new NameExpr(
										"new "
												+ unitTest.testMethods.get(i).parameters
														.get(j).getType()
														.toString() + "()"),
								AssignExpr.Operator.assign);
						ASTHelper.addStmt(block, methodCallVariableAssign);
						ASTHelper.addArgument(methodCallExpr, new NameExpr(
								"var" + j));
					} else {
						ArrayList<VariableDeclarator> variables = new ArrayList<>();
						VariableDeclarator methodCallVariableDeclaration = new VariableDeclarator(
								new VariableDeclaratorId("mockVar" + j));
						variables.add(methodCallVariableDeclaration);
						VariableDeclarationExpr methodCallVariableExpr = new VariableDeclarationExpr(
								ASTHelper
										.createReferenceType(
												unitTest.testMethods.get(i).parameters
														.get(j).getType()
														.toString(), 0),
								variables);
						ASTHelper.addStmt(block, methodCallVariableExpr);
						AssignExpr methodCallVariableAssign = new AssignExpr(
								new NameExpr("mockVar" + j),
								new NameExpr(
										"EasyMock.createMock("
												+ unitTest.testMethods.get(i).parameters
														.get(j).getType()
														.toString() + ".class)"),
								AssignExpr.Operator.assign);
						ASTHelper.addStmt(block, methodCallVariableAssign);
						ASTHelper.addArgument(methodCallExpr, new NameExpr(
								"mockVar" + j));
					}
				}
			}
			ASTHelper.addStmt(block, methodCallExpr);
			method.setBody(block);
		}
		return cu;
	}
}