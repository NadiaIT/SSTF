package com.java.module.sourceParser;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;

public class Method {
	public String methodName;
	public BlockStmt methodBody;
	public MethodDeclaration compilationUnit;
	public int startingLine;
	public int endingLine;
	public List<AnnotationExpr> annotation;
	public List<NameExpr> throwed;
	public List<Parameter> parameters;
}
