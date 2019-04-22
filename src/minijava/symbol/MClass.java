package minijava.symbol;

import java.util.*;

import minijava.syntaxtree.*;
import minijava.visitor.*;
import util.ErrorHandler;

public class MClass extends MType {
	// attributes
	private String name_ = null;
	private String father_name_ = null;
	private MClass father_ = null;
	private int size_ = 0;
	private Boolean is_main_class = false;
	
	// symbol tables
	private HashMap<String, MMethod> methods_ = new HashMap<String, MMethod>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	// The method view of current class
	// The "String" should be of format: <ClassName>_<MethodName>
	private HashMap<String, Integer> method_offset_ = new HashMap<String, Integer>();
	// The variable view of current class
	// The "String" should be of format: <ClassName>_<VariableName>
	private HashMap<String, Integer> var_offset_ = new HashMap<String, Integer>();
	
	// All the methods and variables in the memory
	private ArrayList<String> all_methods_ = new ArrayList<String>();
	private ArrayList<String> all_vars_ = new ArrayList<String>();
	
	public MClass(Node node) {
		String errorMsg = "";
		if (node instanceof ClassDeclaration) {
			ClassDeclaration class_node = (ClassDeclaration) node;
			// System.out.println("Declare: " + (class_node.f1.f0.toString()));
			name_ = class_node.f1.f0.toString();
			if (SymbolTable.isReserved(name_)) {
				errorMsg = "Use reserved words!";
				ErrorHandler.errorPrint(errorMsg);
			}
			
			father_ = null;
			if (!SymbolTable.parseVar(class_node.f3, vars_)) {
				System.out.println("in class " + name_);
				System.exit(1);
			}
			// SetOwner for MVar
			for (String var_name : vars_.keySet()) {
				vars_.get(var_name).setOwner(this);
			}
			parseMethod(class_node.f4);
		} else if (node instanceof ClassExtendsDeclaration) {
			ClassExtendsDeclaration class_node = (ClassExtendsDeclaration) node;
			// System.out.println("Declare: " + ((ClassExtendsDeclaration) node).f1.f0.toString());
			name_ = class_node.f1.f0.toString();
			if (SymbolTable.isReserved(name_)) {
				errorMsg = "Use reserved words!";
				ErrorHandler.errorPrint(errorMsg);
			}
			
			father_name_ = class_node.f3.f0.toString();
			if (!SymbolTable.parseVar(class_node.f5, vars_)) {
				System.out.println("in class " + name_);
				System.exit(1);
			}
			// SetOwner for MVar
			for (String var_name : vars_.keySet()) {
				vars_.get(var_name).setOwner(this);
			}
			parseMethod(class_node.f6);
		} else if (node instanceof MainClass) {
			is_main_class = true;
			// System.out.println("Declare: " + ((MainClass) node).f1.f0.toString());
			MainClass class_node = (MainClass) node;
			name_ = class_node.f1.f0.toString();
			if (SymbolTable.isReserved(name_)) {
				System.exit(1);
			}
			
			methods_.put("main", new MMethod(this, (MainClass) node));
		}
	}
	
	private void parseMethod(NodeListOptional method_list) {
		String errorMsg = "";
		for (Node node : method_list.nodes) {
			String method_name = ((MethodDeclaration) node).f2.f0.toString();
			if (vars_.containsKey(method_name)) {
				errorMsg = "method " + method_name + " is also a variable ??";
				ErrorHandler.errorPrint(errorMsg);
			}
			if (methods_.containsKey(method_name)) {
				errorMsg = "Duplicate declaration of method " + method_name;
				ErrorHandler.errorPrint(errorMsg);
			} else {
				methods_.put(method_name, new MMethod(this, node));
			}
		}
	}
	
	private MClass findFather(){
		return SymbolTable.queryClass(father_name_);
	}

	public void fillBack() {
		MVar var = null;
		MMethod method = null;
		String errorMsg = "";
		
		// Check whether the type of the variables in the class scope has been defined and register variable
		for (String name : vars_.keySet()) {
			var = vars_.get(name);
			if (var.getType() instanceof MUndefined) {
				MType type = SymbolTable.queryClass(((MUndefined)var.getType()).getClassName());
				if (type == null) {
					errorMsg = "Using undefined class " + ((MUndefined)var.getType()).getClassName();
					ErrorHandler.errorPrint(errorMsg);
				} else {
					vars_.get(name).setType(type);
				}
			}
		}
		
		// Check whether the return type of a method has been defined and register
		for (String name : methods_.keySet()) {
			method = methods_.get(name);
			if (method.getRetType() instanceof MUndefined) {
				MType type = SymbolTable.queryClass(((MUndefined)method.getRetType()).getClassName());
				if (type == null) {
					errorMsg = "Using undefined class " + ((MUndefined)method.getRetType()).getClassName();
					ErrorHandler.errorPrint(errorMsg);
				} else {
					methods_.get(name).setRetType(type);
				}
			}
			method.fillBack();
		}
	}
	
	public void registerFather() {
		// check the extension loop
		MClass father;
		String errorMsg = "";

		if(father_name_ == null) {
			return;
		}
		father_ = findFather();
		if(father_ == null) {
			errorMsg = "The father "+ father_name_ + " of class " +  getName() + " is not defined!";
			ErrorHandler.errorPrint(errorMsg);
		}

		
		father = father_;
		while(father != null) {
			if(father.getName().equals(getName())) {
				errorMsg = "Extension loop found in class: " + father_.getName() + " and " + getName();
				ErrorHandler.errorPrint(errorMsg);
			}
			father = father.getFather();
		}
	}

	// TODO: Carefully check the problems of function overloading
	public void registerMethod() {
		String errorMsg = "";
		// check for multiple definitions
		for(HashMap.Entry<String, MMethod> m : methods_.entrySet()) {
			MClass father = getFather();
			while(father != null) {
				if(father.getMethod().containsKey(m.getKey())) {
					// Check the parameters (Override is allowed but overload is not allowed)
					if (!father.getMethod().get(m.getKey()).matchParam(m.getValue()) || 
						!father.getMethod().get(m.getKey()).getRetType().isAssignable(m.getValue().getRetType())) {
						errorMsg = "Dupicative definition of function: " + m.getKey() 
							+ " in father class " + father.getName() +" and class " + getName();
						ErrorHandler.errorPrint(errorMsg);
					}
				}
				father = father.getFather();
			}
			m.getValue().register();
		}
	}

	public void registerVar() {
		String errorMsg = "";
		for(HashMap.Entry<String, MVar> v : vars_.entrySet()) {
			MClass father = getFather();
			while(father != null) {
				if(father.getMethod().containsKey(v.getKey())) {
					errorMsg = "Dupicative definition of variable: " + v.getKey() 
						+ " in father class " + father.getName() +" and class " + getName();
					ErrorHandler.errorPrint(errorMsg);
				}
				father = father.getFather();
			}
		}
	}

	public void register() {
		registerMethod();
		registerVar();
	}

	public MVar queryVar(String var_name) {
		if (vars_.containsKey(var_name)) {
			return vars_.get(var_name);
		} else {
			if (father_ != null)
				return father_.queryVar(var_name);
			else
				return null;
		}
	}
	
	public MMethod queryMethod(String method_name) {
		if (methods_.containsKey(method_name)) {
			return methods_.get(method_name);
		} else {
			if (father_ != null)
				return father_.queryMethod(method_name);
			else
				return null;
		}
	}
	
	public void checkMethods() {
		
	}
	
	public String getName() {
		return name_;
	}

	public int getSize() {
		return 0;
	}

	public String getFatherName() {
		return this.father_name_;
	}

	public void setFatherName(String father_name) {
		this.father_name_ = father_name;
	}

	public void setFather(MClass father) {
		this.father_ = father;
	}

	public MClass getFather() {
		return father_;
	}

	public HashMap<String, MMethod> getMethod() {
		return methods_;
	}

	public boolean isAssignable(MType src) {
		if (!(src instanceof MClass)) return false;
		MClass cur = (MClass) src;
		while (!cur.getName().equals(this.name_) && (cur.getFather() != null)) {
			cur = cur.getFather();
		}
		return cur.getName().equals(this.name_);
	}
	
	public boolean isMainClass() {
		return is_main_class;
	}

	public int queryMethodOffset(String method_name) {
		return method_offset_.get(method_name);
	}
	
	public int queryVarOffset(String var_name) {
		return var_offset_.get(var_name);
	}

	// code below for piglet code generation
	public String generatePigletNewClassCode() {
		createView();
		String code = "new_" + name_ + "\n";
		int allocate_size, cur;
		
		code += " [0]\n";
		code += "BEGIN\n";
		
		// Allocate space for VTable
		allocate_size = 4 * (1 + all_vars_.size());
		code += "MOVE TEMP 0 HALLOCATE " + allocate_size + "\n";
		cur = 4;
		for (String var_name : all_vars_) {
			// TEMP 1 stores the current variable's address
			code += "MOVE TEMP 1 PLUS TEMP 0 " + cur + "\n";
			// Initialize all the variables to zero
			code += "HSTORE TEMP 1 0 0\n";
			cur += 4;
		}
		
		// Allocate space for MTable
		allocate_size = 4 * all_methods_.size();
		code += "MOVE TEMP 1 HALLOCATE " + allocate_size + "\n";
		code += "HSTORE TEMP 0 0 TEMP 1";
		cur = 0;
		for (String method_name : all_methods_) {
			// TEMP 2 stores the current method's address
			code += "MOVE TEMP 2 PLUS TEMP 1 " + cur + "\n";
			code += "MOVE TEMP 3 " + method_name + "\n";
			code += "HSTORE TEMP 2 0 TEMP 3\n";
			cur += 4;
		}
		
		code += "RETURN\n";
		code += "RETURN\n";
		code += "	TEMP 0\n";
		code += "END";
		return code;
	}
	

	// This function will generate the method_offset_ and the var_offset_
	public void createView() {
		ArrayList<MClass> father_list = new ArrayList<MClass>();
		MClass cur_class = this;
		int moffset = 0, voffset = 4;  // The offset in the view
		
		// Generate the father_list;
		while (cur_class != null) {
			father_list.add(cur_class);
			cur_class = cur_class.father_;
		}
		
		Collections.reverse(father_list);
		for (MClass cur : father_list) {
			for (String method_name : cur.methods_.keySet()) {
				method_offset_.put(cur.name_ + "_" + method_name, moffset);
				all_methods_.add(cur.name_ + "_" + method_name);
				moffset += 4;
			}
			for (String var_name : cur.vars_.keySet()) {
				var_offset_.put(cur.name_ + "_" + var_name, voffset);
				all_vars_.add(cur.name_ + "_" + var_name);
				voffset += 4;
			}
		}
		
		father_list.clear();
	}
}
