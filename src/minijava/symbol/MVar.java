package minijava.symbol;

import minijava.syntaxtree.*;

public class MVar {
	private MType type_;
	private String name_;
	
	public MVar(Node node) {
		if (node instanceof VarDeclaration) {
			VarDeclaration declare = (VarDeclaration) node;
			if (declare.f0 instanceof 
		} else {
			System.out.println("Error in MVar(): Not an VarDeclaration.");
			System.exit(1);
		}
	}
	
	public String getName() {
		return name_;
	}
}
