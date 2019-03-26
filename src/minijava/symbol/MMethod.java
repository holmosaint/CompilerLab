package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.typecheck.*;

public class MMethod extends MScope {
	
	private MType ret_type_;
	private String name_;
	private HashMap<String, MVar> params_ = new HashMap<String, MVar>();
	private HashMap<Integer, String> index2name_ = new HashMap<Integer, String>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	private ArrayList<MBlock> blocks_ = new ArrayList<MBlock>();
	private MClass owner_;
	private MScope father_ = null;
	private MExpr return_;
	
	public MMethod(MClass owner, Node node) {
		owner_ = owner;
		MethodDeclaration declare = (MethodDeclaration) node;
		ret_type_ = SymbolTable.getType(declare.f1.f0.which);
		name_ = declare.f2.f0.toString();
		if (SymbolTable.isReserved(name_)) {
			System.out.println("Using reserved name");
			System.exit(1);
		}
		
		System.out.println("You declare method " + name_);
		parseParam(declare.f4);
		if (!SymbolTable.parseVar(declare.f7, vars_)) {
			System.out.println("in method " + name_);
			System.exit(1);
		}
		parseStatement(declare.f8, this);
		return_ = new MExpr(declare.f10, this);
	}
	
	// Typical constructor for MainClass
	public MMethod(MClass owner, MainClass class_node) {
		owner_ = owner;
		name_ = "main";
		
		String param_name = class_node.f11.f0.toString();
		index2name_.put(0, param_name);
		params_.put(param_name, new MVar(param_name));
		vars_.put(param_name, new MVar(param_name));
		SymbolTable.parseVar(class_node.f14, vars_);
		
		parseStatement(class_node.f15, this);
		
		ret_type_ = null;
		return_ = null;
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
			System.exit(1);
		} else {
			params_.put(param_name, new MVar(param));
		}
		// WARNING! Something could go wrong here
		if (vars_.containsKey(param_name)) {
			System.exit(1);
		} else {
			vars_.put(param_name, new MVar(param));
		}
		index2name_.put(0, param_name);
		
		NodeListOptional rest_params_list = ((FormalParameterList) param_list.node).f1;
		
		// Parse the rest parameters
		int i = 1;
		for (Node node : rest_params_list.nodes) {
			FormalParameterRest declare = (FormalParameterRest) node;
			param_name = declare.f1.f1.f0.toString();
			if (params_.containsKey(param_name)) {
				System.out.println("Duplicate declaration of parameter " + param_name);
				System.exit(1);
			} else {
				params_.put(param_name, new MVar(declare.f1));
			}
			if (vars_.containsKey(param_name)) {
				System.exit(1);
			} else {
				vars_.put(param_name, new MVar(declare.f1));
			}
			index2name_.put(i, param_name);
			i++;
		}
	}

	public MVar queryVar(String var_name) {
		if (vars_.containsKey(var_name)) {
			return vars_.get(var_name);
		}
		else {
			return owner_.queryVar(var_name);
		}
	}

	public void register() {
		System.out.println("This is method " + name_);
		for(MBlock b : blocks_) {
			b.register();
		}
		if (return_ != null) {
			return_.register();
		}
	}

	public MClass getOwner() {
		return this.owner_;
	}

	public MScope getFather() {
		return null;
	}
	
	public MType getRetType() {
		return ret_type_;
	}
	
	public boolean matchParam(ArrayList<MExpr> exprs) {
		// First, check the number of parameters
		if (params_.size() != exprs.size()) {
			System.out.println("In method " + name_ + " number of parameters does not match: " + "get " + 
							   exprs.size() + ", expect " + params_.size());
			System.exit(1);
		}
		// Then, check each parameter
		for (int i = 0; i < params_.size(); i++) {
			if (!params_.get(index2name_.get(i)).getType().isAssignable(exprs.get(i).getType())) {
				System.out.println("Type mismatch in method's parameters");
				System.exit(1);
			}
		}
		
		return true;
	}
}
