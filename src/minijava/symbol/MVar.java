package minijava.symbol;

import java.util.HashMap;

import minijava.syntaxtree.*;
import util.ErrorHandler;

public class MVar {
	private MType type_;
	private String name_;
	private boolean allocated_ = false;
	private int addr_;	// 基址
	private int array_length_ = -1;	// array属性
	private HashMap<String, MVar> var_table_;	// var name 与 MVar之间的map
	private HashMap<String, MMethod> method_table_;	// method 与 addr之间的map	
	
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
	
	public boolean allocate() {
		if (type_ instanceof MClass || type_ instanceof MArray) {
			if (allocated_) return false;
			else {
				allocated_ = true;
				return true;
			}
		} else {
			return false;
		}
	}
	
	// Check if the variable is allocated
	// Only for instance of MClass and MArray
	public boolean isAllocated() {
		if (type_ instanceof MClass || type_ instanceof MArray)
			return allocated_;
		else
			return false;
	}
}
