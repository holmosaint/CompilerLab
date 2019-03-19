package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.typecheck.*;

public class MMethod {
	
	private MType ret_type_;
	private String name_;
	private HashMap<String, MVar> params_ = new HashMap<String, MVar>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	private ArrayList<MBlock> blocks_;
	private MClass owner_;
	
	public MMethod(MClass owner, Node node) {
		owner_ = owner;
		MethodDeclaration declare = (MethodDeclaration) node;
		ret_type_ = SymbolTable.getType(declare.f1.f0.which);
		name_ = declare.f2.f0.toString();
		System.out.println("You declare method " + name_);
		parseParam(declare.f4);
		
		
	}

	public String getName_() {
		return null;
	}

	public int getSize_() {
		return 0;
	}

	public boolean isAssignable(MType target, Node n) {
		return false;
	}
	
	private void parseParam(NodeOptional param_list) {
		System.out.println("has_params = " + param_list.present());
		// Parse the first parameter
		FormalParameter param = ((FormalParameterList) param_list.node).f0;
		String param_name = param.f1.f0.toString();
		if (params_.containsKey(param_name)) {
			System.out.println("Duplicate declaration of parameter " + param_name);
		} else {
			params_.put(param_name, new MVar(param));
		}
		NodeListOptional rest_params_list = ((FormalParameterList) param_list.node).f1;
		
		// Parse the rest parameters
		for (Node node : rest_params_list.nodes) {
			FormalParameterRest declare = (FormalParameterRest) node;
			param_name = declare.f1.f1.f0.toString();
			if (params_.containsKey(param_name)) {
				System.out.println("Duplicate declaration of parameter " + param_name);
			} else {
				params_.put(param_name, new MVar(declare.f1));
			}
		}
	}
}
