package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.visitor.*;

public class MClass extends MType {

	// attributes
	private Identifier name_;
	private int size_ = 0;
	private MClass father_;
	
	// symbol tables
	private HashMap<String, MMethod> methods_ = new HashMap<String, MMethod>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	public MClass(Node n) {
		if (n instanceof MainClass) {
			System.out.println(((MainClass) n).f1.f0.toString());
		} else if (n instanceof ClassDeclaration) {
			System.out.println(((ClassDeclaration) n).f1.f0.toString());
		} else if (n instanceof ClassExtendsDeclaration) {
			System.out.println(((ClassExtendsDeclaration) n).f1.f0.toString());
		}
		
	}
	
	private void AddMethod(MMethod method) {
		
	}
	
	private void AddVar(MVar var) {
		
	}
	
	public void Register() {
		
	}
	
	public void CheckMethods() {
		
	}
	
	public void BuildScope() {
		
	}
	
	public String getName_() {
		return name_.f0.toString();
	}

	public int getSize_() {
		return 0;
	}

	public boolean isAssignable(MType target, Node n) {
		return false;
	}

	
}
