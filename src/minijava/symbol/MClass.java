package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.visitor.*;

public class MClass {
	// attributes
	private String name_;
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
			
		} else if (node instanceof MainClass) {
			System.out.println("Declare: " + ((MainClass) node).f1.f0.toString());
			System.out.println("Are you kidding?");
			MainClass class_node = (MainClass) node;
			name_ = class_node.f1.f0.toString();
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
	
	public void registerFather() {
		// check the extension loop
		MClass father = getFather();
		while(father != null) {
			if(father.getName() == getName()) {
				System.out.printf("Extension loop found in class: [%s] and [%s]\n",
									father.getName(), getName());
				System.exit(1);
			}
			father = father.getFather();
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
	
	public void cuildScope() {
		
	}
	
	public String getName() {
		return name_;
	}

	public int getSize() {
		return 0;
	}

	public MClass getFather() {
		return this.father_;
	}

	public boolean isAssignable(MType target, Node n) {
		return false;
	}


	public HashMap<String, MMethod> getMethod() {
		return methods_;
	}
}
