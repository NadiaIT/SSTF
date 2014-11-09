package com.java.module.sourceParser;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.TypeDeclaration;

public class JavaSourceParser {

	Method constructor;
	PackageDeclaration packageDeclaration;
	List<ImportDeclaration> importDeclarations;
	boolean isInterface;
	boolean isAbstractClass;

	public CompilationUnit getCompilationunit(File fileName) throws IOException {
		InputStream in = null;
		CompilationUnit cu = null;
		constructor = new Method();
		try {
			in = new FileInputStream(fileName);
			cu = JavaParser.parse(in);
			packageDeclaration = cu.getPackage();
			importDeclarations = cu.getImports();
			List<TypeDeclaration> typeDeclarations = cu.getTypes();
			for (TypeDeclaration typeDec : typeDeclarations) {
				List<BodyDeclaration> members = typeDec.getMembers();
				if (typeDec instanceof ClassOrInterfaceDeclaration) {
					ClassOrInterfaceDeclaration c = (ClassOrInterfaceDeclaration) typeDec;
					isInterface = c.isInterface();
					if (c.getModifiers() == 1025) {
						isAbstractClass = true;
					} else
						isAbstractClass = false;
				}
				if (members != null) {
					for (BodyDeclaration member : members) {
						if (member instanceof ConstructorDeclaration) {
							ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) member;
							constructor.annotation = constructorDeclaration
									.getAnnotations();
							constructor.endingLine = constructorDeclaration
									.getEndLine();
							constructor.methodBody = constructorDeclaration
									.getBlock();
							constructor.methodName = constructorDeclaration
									.getName();
							constructor.parameters = constructorDeclaration
									.getParameters();
							constructor.startingLine = constructorDeclaration
									.getBeginLine();
							constructor.throwed = constructorDeclaration
									.getThrows();
						}
					}
				}
			}
		} catch (ParseException x) {
		} finally {
			in.close();
		}
		return cu;
	}
}
