package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.visitor.*;

public class MClass {
	// attributes
	private String name_;
	private String father_name_;
	private MClass father_;
	private int size_ = 0;
	
	// symbol tables
	private HashMap<String, MMethod> methods_ = new HashMap<String, MMethod>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	public MClass(Node node) {
		if (node instanceof ClassDeclaration) {
			ClassDeclaration class_node = (ClassDeclaration) node;
			System.out.println("Declare: " + (class_node.f1.f0.toString()));
			name_ = class_node.f1.f0.toString();
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
			father_ = null;
			methods_.put("main", new MMethod(this, (MainClass) node));
		}
		
	}
	
	private void parseMethod(NodeListOptional method_list) {
		for (Node node : method_list.nodes) {
			String method_name = ((MethodDeclaration) node).f2.f0.toString();
			if (methods_.containsKey(method_name)) {
				System.out.println("Duplicate declaration of method " + method_name);
				System.exit(1);
			} else {
				methods_.put(method_name, new MMethod(this, node));
			}
		}
	}

	private MClass findFather(){
		ArrayList<MClass> class_list = SymbolTable.getClassList();
		for(MClass c : class_list) {
			if(c.getFatherName() == this.father_name_) 
				return c;
		}
		return null;
	}
	
	public void registerFather() {
		// check the extension loop
		String father_name = getFatherName();
		if(father_name == null) {
			return;
		}
		this.father_ = findFather();
		if(this.father_ == null) {
			System.out.printf("The father [%s] of class [%s] is not defined!\n",
								father_name, getName());
			System.exit(1);
		}
		while(father_ != null) {
			if(father_.getName() == getName()) {
				System.out.printf("Extension loop found in class: [%s] and [%s]\n",
									father_.getName(), getName());
				System.exit(1);
			}
			father_ = father_.getFather();
		}
	}

	public void registerMethod() {
		for(HashMap.Entry<String, MMethod> m : methods_.entrySet()) {
			MClass father = getFather();
			while(father != null) {
				if(father.getMethod().containsKey(m.getKey())){
					System.out.printf("Dupicative definition of function: [%s] in class [%s] and class [%s]\n", 
									m.getKey(), father.getName(), getName());
					System.exit(1);
				}
				father = father.getFather();
			}
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
		registerFather();
		registerMethod();
		registerVar();
	}
	
	public void checkMethods() {
		
	}
	
	public void buildScope() {
		
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
	
	public boolean isAssignable(MType target, Node n) {
		return false;
	}


	public HashMap<String, MMethod> getMethod() {
		return methods_;
	}
}
