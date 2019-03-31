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
		// System.out.println("MainClass with name: " + n.f1.f0.toString());
		SymbolTable.addMainClass(new MClass(n));
	}
	
	public void visit(ClassDeclaration n) {
		// System.out.println("ClassDeclaration with name: " + n.f1.f0.toString());
		SymbolTable.addClass(new MClass(n));
	}
	
	public void visit(ClassExtendsDeclaration n) {
		// System.out.println("ClassExtendsDeclaration with name: " + n.f1.f0.toString());
		SymbolTable.addClass(new MClass(n));
	}
}
