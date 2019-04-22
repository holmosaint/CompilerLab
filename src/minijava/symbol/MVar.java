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
	
	// The attributes below will be removed in the future
	private int addr_;      // base address
	// 'method_tabel_' will be updated everytime the variable is assigned,
	// it stores the method accessible for this variable. Each pair 
	// <method_name, class_name> in 'method_table' indicates a unique method
	// which has name <method_name>_<class_name> in the object code 
	private HashMap<String, String> method_table_;  // <method_name : class_name>
	private HashMap<String, Integer> var_index_;    // <class_name : index>
	
	// var_list_ is a concatenation of variable lists in ['real_type_', 
	// 'real_type_' 's father, ... , 'type_', 'type_' 's father ...]
	private ArrayList<MVar> var_list_;
	
	private int tempID = -1;

	// relation between the 'var_index_' and 'var_list_'
	//  class1         class2
	//   ||             ||
	//   \/             \/
	//  var0 var1 var2 var3 var4

	// When method visits one variable, it will inform 'method_table_' for its 
	// current class name first, then uses it to inform var_index_ for index in 
	// the var_list_. So the method can know that currently it access variabels 
	// are in var_list_[index, :]
	
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
		type_ = new MUndefined(param_name_);
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
