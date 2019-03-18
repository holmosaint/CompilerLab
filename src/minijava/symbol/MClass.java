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
			
		} else if (node instanceof ClassExtendsDeclaration) {
			System.out.println(((ClassExtendsDeclaration) n).f1.f0.toString());
		} else if (node instanceof MainClass) {
			System.out.println(((MainClass) node).f1.f0.toString());
			System.out.println("Are you kidding?");
		}
		
	}
	
	
	private void addMethod(MMethod method) {
		
	}
	
	private void addVar(MVar var) {
		
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
