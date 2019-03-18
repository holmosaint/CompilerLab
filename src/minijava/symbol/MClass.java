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

	}

	public void registerMethod() {

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

}
