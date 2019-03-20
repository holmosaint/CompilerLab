package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.typecheck.*;

public class MMethod extends MScope {
	
	private MType ret_type_;
	private String name_;
	private HashMap<String, MVar> params_ = new HashMap<String, MVar>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	private ArrayList<MBlock> blocks_ = new ArrayList<MBlock>();
	private MClass owner_;
	private MExpr return_;
	
	public MMethod(MClass owner, Node node) {
		owner_ = owner;
		MethodDeclaration declare = (MethodDeclaration) node;
		ret_type_ = SymbolTable.getType(declare.f1.f0.which);
		name_ = declare.f2.f0.toString();
		System.out.println("You declare method " + name_);
		parseParam(declare.f4);
		if (!SymbolTable.parseVar(declare.f7, vars_)) {
			System.out.println("in method " + name_);
			System.exit(1);
		}
		parseStatement(declare.f8, this);
		return_ = new MExpr(declare.f10);
	}

	public String getName() {
		return name_;
	}
	
	public void addBlock(MScope block) {
		System.out.println("MMethod " + name_ + " add an MBlock");
		blocks_.add((MBlock) block);
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
