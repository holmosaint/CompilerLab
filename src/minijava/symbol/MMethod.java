package minijava.symbol;

import java.util.*;

import minijava.syntaxtree.*;
import minijava2piglet.minijava2piglet;
import util.ErrorHandler;

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
		ret_type_ = SymbolTable.getType(declare.f1);
		name_ = declare.f2.f0.toString();
		if (SymbolTable.isReserved(name_)) {
			ErrorHandler.errorPrint("Use reserved name " + name_);
		}
		
		// System.out.println("You declare method " + name_);
		parseParam(declare.f4);
		SymbolTable.parseVar(declare.f7, vars_);
		parseStatement(declare.f8, this);
		return_ = new MExpr(declare.f10, this);
	}
	
	// Typical constructor for MainClass's main method
	public MMethod(MClass owner, MainClass class_node) {
		owner_ = owner;
		name_ = "main";
		
		// TODO: Is this parameter available for the method?
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
		// System.out.println("MMethod " + name_ + " add an MBlock");
		blocks_.add((MBlock) block);
	}
	
	public ArrayList<MBlock> getBlockList() {
		return blocks_;
	}
	
	private void parseParam(NodeOptional param_list) {
		// System.out.println("has_params = " + param_list.present());
		if (param_list.present() == false) return;
		// Parse the first parameter
		FormalParameter param = ((FormalParameterList) param_list.node).f0;
		String param_name = param.f1.f0.toString();
		MVar var;
		
		var = new MVar(param);
		var.assign();
		
		if (params_.containsKey(param_name)) {
			ErrorHandler.errorPrint("Duplicate declaration of parameter " + param_name);
		} else {
			params_.put(param_name, var);
		}
		// WARNING! Something could go wrong here
		if (vars_.containsKey(param_name)) {
			ErrorHandler.errorPrint("Duplicate declaration of parameter" + param_name);
		} else {
			vars_.put(param_name, var);
		}
		index2name_.put(0, param_name);
		
		NodeListOptional rest_params_list = ((FormalParameterList) param_list.node).f1;
		
		// Parse the rest parameters
		int i = 1;
		for (Node node : rest_params_list.nodes) {
			FormalParameterRest declare = (FormalParameterRest) node;
			param_name = declare.f1.f1.f0.toString();
			
			var = new MVar(declare.f1);
			var.assign();
			if (params_.containsKey(param_name)) {
				ErrorHandler.errorPrint("Duplicate declaration of parameter " + param_name);
			} else {
				params_.put(param_name, var);
			}
			if (vars_.containsKey(param_name)) {
				ErrorHandler.errorPrint("Duplicate declaration of parameter " + param_name);
			} else {
				vars_.put(param_name, var);
			}
			index2name_.put(i, param_name);
			i++;
		}
	}

	public void fillBack() {
		MVar var = null;
		
		// Check the type of the variables in a method and register 
		for (String name : vars_.keySet()) {
			var = vars_.get(name);
			if (var.getType() instanceof MUndefined) {
				MType type = SymbolTable.queryClass(((MUndefined)var.getType()).getClassName());
				if (type == null) {
					ErrorHandler.errorPrint("Using undefined class " + ((MUndefined)var.getType()).getClassName());
				} else {
					vars_.get(name).setType(type);
				}
			}
		}
		
		// Check the type of the variables in a method and register
		for (String name : params_.keySet()) {
			var = params_.get(name);
			if (var.getType() instanceof MUndefined) {
				MType type = SymbolTable.queryClass(((MUndefined)var.getType()).getClassName());
				if (type == null) {
					ErrorHandler.errorPrint("Using undefined class " + ((MUndefined)var.getType()).getClassName());
				} else {
					params_.get(name).setType(type);
				}
			}
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
		// System.out.println("Registering method " + name_);
		for(MBlock b : blocks_) {
			b.register();
		}
		if (return_ != null) {
			return_.register();
			if (!ret_type_.isAssignable(return_.getType())) {
				ErrorHandler.errorPrint(name_ + "'s return expression's type is wrong: " +
										"expected " + ret_type_.getName() + ", but " +
						                "get " + return_.getType().getName());
			}
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
	
	public void setRetType(MType type) {
		ret_type_ = type;
	}
	
	public boolean matchParam(ArrayList<MExpr> exprs) {
		// First, check the number of parameters
		if (params_.size() != exprs.size()) {
			ErrorHandler.errorPrint("In method " + name_ + " number of parameters does not match: " + "get " + 
							   exprs.size() + ", expect " + params_.size());
		}
		// Then, check each parameter
		for (int i = 0; i < params_.size(); i++) {
			if (!params_.get(index2name_.get(i)).getType().isAssignable(exprs.get(i).getType())) {
				ErrorHandler.errorPrint("Type mismatch in " + name_ + "'s parameters: " 
							   	   + params_.get(index2name_.get(i)).getType().getName() + " "
							   	   + params_.get(index2name_.get(i)).getName() + " "
							   	   + exprs.get(i).getType().getName() + " " + exprs.get(i).getWhich());
			}
		}
		return true;
	}
	
	public HashMap<String, MVar> getParams() {
		return params_;
	}
	
	public HashMap<Integer, String> getIndex2Name() {
		return index2name_;
	}
	
	public boolean matchParam(MMethod method) {
		HashMap<String, MVar> params = method.getParams();
		HashMap<Integer, String> index2name = method.getIndex2Name();
		if (params_.size() != params.size()) {
			return false;
		}
		
		for (int i = 0; i < params.size(); i++) {
			if (!params_.get(index2name_.get(i)).getType().getName().equals(
					params.get(index2name.get(i)).getType().getName()))
				return false;
		}
		
		return true;
	}
	
	public HashMap<String, MVar> getVarMap() {
		return vars_;
	}
	
	// below for piglet code generation
	public String generatePigletMethodCode() {
		MClass c = getOwner();
		String code = c.getName() + "_" + getName();
		int parameterLength = params_.keySet().size();
		++parameterLength; // 第一个参数是VTable
		code += " [" + parameterLength + "]\n";
		minijava2piglet.writeCode(code);
		
		code = "";
		
		// 分配params的TEMP寄存器，从1开始
		for(int i = 0;i < params_.size(); ++i) {
			String param_name = index2name_.get(Integer.valueOf(i));
			MVar var = params_.get(param_name);
			var.setTempID(i + 1);
		}
		
		// 分配TEMP给各个局部变量
		for(String var_name : getVarMap().keySet()) {
			MVar var = queryVar(var_name);
			var.setTempID(minijava2piglet.getTempIndex());
		}
		
		int tab = 1; // tab的数量
		for(int i = 0;i < blocks_.size(); ++i) {
			blocks_.get(i).generatePigletBlockCode(tab, true); // code should be written in the block generation function
		}

		// return expression
		code = "";
		if(!c.isMainClass()) {
			String returnTemp = "";
			returnTemp = return_.generatePigletExpressionCode(0, true);
			code += "RETURN " + returnTemp + "\n";
		}
		
		code += "END\n\n";
		minijava2piglet.writeCode(code);
		
		return code;
	}
	
	// 判断是不是局部变量（参数与局部变量）
	public boolean judgeLocalVar(MVar var) {
		return params_.containsKey(var.getName()) || vars_.containsKey(var.getName()); 
	}
}
