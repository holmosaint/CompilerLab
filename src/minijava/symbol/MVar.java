package minijava.symbol;

import java.util.ArrayList;
import java.util.HashMap;

import minijava.syntaxtree.FormalParameter;
import minijava.syntaxtree.Node;
import minijava.syntaxtree.VarDeclaration;
import util.ErrorHandler;

public class MVar {
	private MType type_;
	private String name_;
	private boolean assigned_ = false;
	
	private int length_;  // for array instance
	private MClass real_type_;  // for class instance	
	private MClass class_owner_ = null;
	
	private int tempID = -1;
	
	public MVar(MType type) {
		type_ = type;
		name_ = "";
	}
	
	public MVar(Node node) {
		if (node instanceof VarDeclaration) {
			VarDeclaration declare = (VarDeclaration) node;
			name_ = declare.f1.f0.toString();
			type_ = SymbolTable.getType(declare.f0);
		} else if (node instanceof FormalParameter){
			FormalParameter declare = (FormalParameter) node;
			name_ = declare.f1.f0.toString();
			type_ = SymbolTable.getType(declare.f0);
		} else {
			ErrorHandler.errorPrint("Error in MVar(): Not an VarDeclaration.");
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
	
	public void setType(MType type) {
		type_ = type;
	}
	
	public MType getType() {
		return type_;
	}
	
	public boolean assign() {
		if (assigned_) {
			return false;
		} else {
			assigned_ = true;
			return true;
		}
	}
	
	public void allocate(MExpr expr) {
		if (type_ instanceof MArray) {
			length_ = expr.arrayLength();
		} else if (type_ instanceof MClass) {
			real_type_ = (MClass) expr.getType();
		}
	}
	
	public void setOwner(MClass class_owner) {
		class_owner_ = class_owner;
	}
	
	public MClass getOwner() {
		return class_owner_;
	}
	
	// Check if the variable is assigned
	public boolean isAssigned() {
		return assigned_;
	}

	public int getTempID() {
		if(tempID < 0)
			ErrorHandler.errorPrint("Temp register has not been allocated to variable " + getName());
		return tempID;
	}

	public void setTempID(int tempID) {
		if(this.tempID >= 0)
			ErrorHandler.errorPrint("Duplicate TEMP register allocation to variable " + getName());
		this.tempID = tempID;
	}
	
	public int getLength() {
		if (!(type_ instanceof MArray)) {
			System.out.println("Error in getLength");
			System.exit(1);
		}
		return length_;
	}
}
