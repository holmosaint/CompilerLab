package minijava.typecheck;

import minijava.syntaxtree.*;
import minijava.visitor.*;
import minijava.symbol.*;

public class ClassTreeBuilder extends DepthFirstVisitor {
	private String file_name_ = "";
	
	public ClassTreeBuilder(String file_name) {
		file_name_ = file_name;
	}
	
	public void visit(MainClass n) {
		System.out.println("MainClass");
		System.out.println(n.f1.f0.toString());
	}
	
	public void visit(ClassDeclaration n) {
		System.out.println("ClassDeclaration");
		System.out.println(n.f1.f0.toString());
	}
	
	public void visit(ClassExtendsDeclaration n) {
		System.out.println("ClassExtendsDeclaration");
		System.out.println(n.f1.f0.toString());
	}
}
