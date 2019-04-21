package minijava.symbol;

import minijava.syntaxtree.*;
import util.ErrorHandler;
import java.util.*;

public class MVar {
	private MType type_;
	private String name_;
	private boolean assigned_ = false;

	private int addr_;      // base address
	private int length_;    // for array

	private MClass real_type_;  // for class instance
	// 'method_tabel_' will be updated everytime the variable is assigned,
	// it stores the method accessible for this variable. Each pair 
	// <method_name, class_name> in 'method_table' indicates a unique method
	// which has name <method_name>_<class_name> in the object code 
	private HashMap<String, String> method_table_;  // <method_name : class_name>
	private HashMap<String, Integer> var_index_;    // <class_name : index>
	// var_list_ is a concatenation of variable lists in ['real_type_', 
	// 'real_type_' 's father, ... , 'type_', 'type_' 's father ...]
	private ArrayList<MVar> var_list_;

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
		if (type_ instanceof MClass) {
			// TODO: Allocate and initialize variables for MClass
		} else if (type_ instanceof MArray) {
			// TODO: Get length for array
		}
		
		if (assigned_) {
			return false;
		} else {
			assigned_ = true;
			return true;
		}
	}
	
	// Check if the variable is allocated
	// Only for instance of MClass and MArray
	public boolean isAssigned() {
		return assigned_;
	}
}
