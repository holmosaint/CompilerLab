package minijava.symbol;

import minijava.syntaxtree.*;

public class MVar {
	private MType type_;
	private String name_;
	
	public MVar(Node node) {
		if (node instanceof VarDeclaration) {
			VarDeclaration declare = (VarDeclaration) node;
			name_ = declare.f1.f0.toString();
			type_ = SymbolTable.getType(declare.f0.f0.which);
		} else if (node instanceof FormalParameter){
			FormalParameter declare = (FormalParameter) node;
			name_ = declare.f1.f0.toString();
			type_ = SymbolTable.getType(declare.f0.f0.which);
		} else {
			System.out.println("Error in MVar(): Not an VarDeclaration.");
			System.exit(1);
		}
	}
	
	// Typical constructor for MainClass's parameter
	public MVar(String param_name_) {
		type_ = null;
		name_ = param_name_;
	}

	public String getName() {
		return name_;
	}
	
	public MType getType() {
		return type_;
	}
}
