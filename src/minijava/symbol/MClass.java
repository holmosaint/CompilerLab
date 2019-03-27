package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.visitor.*;

public class MClass extends MType {
	// attributes
	private String name_ = null;
	private String father_name_ = null;
	private MClass father_ = null;
	private int size_ = 0;
	
	// symbol tables
	private HashMap<String, MMethod> methods_ = new HashMap<String, MMethod>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	public MClass(Node node) {
		if (node instanceof ClassDeclaration) {
			ClassDeclaration class_node = (ClassDeclaration) node;
			System.out.println("Declare: " + (class_node.f1.f0.toString()));
			name_ = class_node.f1.f0.toString();
			if (SymbolTable.isReserved(name_)) {
				System.exit(1);
			}
			
			father_ = null;
			if (!SymbolTable.parseVar(class_node.f3, vars_)) {
				System.out.println("in class " + name_);
				System.exit(1);
			}
			parseMethod(class_node.f4);
		} else if (node instanceof ClassExtendsDeclaration) {
			ClassExtendsDeclaration class_node = (ClassExtendsDeclaration) node;
			System.out.println("Declare: " + ((ClassExtendsDeclaration) node).f1.f0.toString());
			name_ = class_node.f1.f0.toString();
			if (SymbolTable.isReserved(name_)) {
				System.exit(1);
			}
			
			father_name_ = class_node.f3.f0.toString();
			if (!SymbolTable.parseVar(class_node.f5, vars_)) {
				System.out.println("in class " + name_);
				System.exit(1);
			}
			parseMethod(class_node.f6);
		} else if (node instanceof MainClass) {
			System.out.println("Declare: " + ((MainClass) node).f1.f0.toString());
			MainClass class_node = (MainClass) node;
			name_ = class_node.f1.f0.toString();
			if (SymbolTable.isReserved(name_)) {
				System.exit(1);
			}
			
			methods_.put("main", new MMethod(this, (MainClass) node));
		}
		
	}
	
	private void parseMethod(NodeListOptional method_list) {
		for (Node node : method_list.nodes) {
			String method_name = ((MethodDeclaration) node).f2.f0.toString();
			if (vars_.containsKey(method_name)) {
				System.out.println("method " + method_name + " is also a variable ??");
				System.exit(1);
			}
			if (methods_.containsKey(method_name)) {
				System.out.println("Duplicate declaration of method " + method_name);
				System.exit(1);
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
		
		for (String name : vars_.keySet()) {
			var = vars_.get(name);
			if (var.getType() instanceof MUndefined) {
				MType type = SymbolTable.queryClass(((MUndefined)var.getType()).getClassName());
				if (type == null) {
					System.out.println("Using undefined class " + ((MUndefined)var.getType()).getClassName());
					System.exit(1);
				} else {
					vars_.get(name).setType(type);
				}
			}
		}
		
		for (String name : methods_.keySet()) {
			method = methods_.get(name);
			if (method.getRetType() instanceof MUndefined) {
				MType type = SymbolTable.queryClass(((MUndefined)method.getRetType()).getClassName());
				if (type == null) {
					System.out.println("Using undefined class " + ((MUndefined)method.getRetType()).getClassName());
					System.exit(1);
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

		if(father_name_ == null) {
			return;
		}
		father_ = findFather();
		if(father_ == null) {
			System.out.printf("The father [%s] of class [%s] is not defined!\n",
								father_name_, getName());
			System.exit(1);
		}

		
		father = father_;
		while(father != null) {
			if(father.getName().equals(getName())) {
				System.out.printf("Extension loop found in class: [%s] and [%s]\n",
									father_.getName(), getName());
				System.exit(1);
			}
			father = father.getFather();
		}
	}

	// TODO: Carefully check the problems of function overloading
	public void registerMethod() {
		// check for multiple definitions
		for(HashMap.Entry<String, MMethod> m : methods_.entrySet()) {
			MClass father = getFather();
			while(father != null) {
				if(father.getMethod().containsKey(m.getKey())){
					// Check the parameters
					if (!father.getMethod().get(m.getKey()).matchParam(m.getValue())) {
						System.out.printf("Dupicative definition of function: [%s] in class [%s] and class [%s]\n", 
										m.getKey(), father.getName(), getName());
						System.exit(1);
					}
				}
				father = father.getFather();
			}
			m.getValue().register();
		}
	}

	public void registerVar() {
		for(HashMap.Entry<String, MVar> v : vars_.entrySet()) {
			MClass father = getFather();
			while(father != null) {
				if(father.getMethod().containsKey(v.getKey())){
					System.out.printf("Dupicative definition of variable: [%s] in class [%s] and class [%s]\n", 
									v.getKey(), father.getName(), getName());
					System.exit(1);
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

	public boolean isAssignable(MType target) {
		if (!(target instanceof MClass)) return false;
		MClass cur = (MClass) target;
		while (!cur.getName().equals(this.name_) && (cur.getFather() != null)) {
			cur = cur.getFather();
		}
		return cur.getName().equals(this.name_);
	}

}
