package minijava.symbol;

import minijava.syntaxtree.*;
import minijava2piglet.minijava2piglet;
import util.*;

public class MPrimExpr {
	// 0->IntegerLiteral
	// 1->TrueLiteral
	// 2->FalseLiteral
	// 3->Identifier
	// 4->ThisExpression
	// 5->ArrayAllocationExpression
	// 6->AllocationExpression
	// 7->NotExpression
	// 8->BracketExpression
	private MScope father_;
	private int which_;
	private String literal_ = null;
	private MVar var_ = null;
	private String var_name_ = null;
	private MExpr expr_ = null;
	private MType type_ = null;
	
	public MPrimExpr(PrimaryExpression prim_expr, MScope father) {
		father_ = father;
		which_ = prim_expr.f0.which;
		
		switch (which_) {
		case 0:
			// IntegerLiteral
			literal_ = ((IntegerLiteral) prim_expr.f0.choice).f0.toString();
			break;
		case 1:
			// TrueLiteral
			literal_ = "true";
			break;
		case 2:
			// FalseLiteral
			literal_ = "false";
			break;
		case 3:
			// Identifier
			var_name_ = ((Identifier) prim_expr.f0.choice).f0.toString();
			break;
		case 4:
			// ThisExpression
			literal_ = "this";
			var_name_ = "this";
			break;
		case 5:
			// ArrayAllocationExpression
			expr_ = new MExpr(((ArrayAllocationExpression) prim_expr.f0.choice).f3, father_);
			break;
		case 6:
			// AllocationExpression
			literal_ = ((AllocationExpression) prim_expr.f0.choice).f1.f0.toString();
			var_name_ = "";
			break;
		case 7:
			// NotExpression
			expr_ = new MExpr(((NotExpression) prim_expr.f0.choice).f1, father_);
			break;
		case 8:
			// BracketExpression
			expr_ = new MExpr(((BracketExpression) prim_expr.f0.choice).f1, father_);
			break;
		default:
			break;
		}
	}

	public void register() {
		String errorMsg = "";
		switch (which_) {
			case 0:
				// IntegerLiteral
				type_ = SymbolTable.getType("int");
				return;
			case 1:
				// TrueLiteral
				type_ = SymbolTable.getType("boolean");
				return;
			case 2:
				// FalseLiteral
				type_ = SymbolTable.getType("boolean");
				return;
			case 3:
				// Identifier
				var_ = father_.queryVar(var_name_);
				if (var_ == null) {
					errorMsg = "Use undefined variable " + var_.getName();
					break;
				} else if (!var_.isAssigned()) {
					errorMsg = "Use uninitialized variable " + var_.getName();
					break;
				}
				type_ = var_.getType();
				return;
			case 4:
				// ThisExpression
				MScope fatherScope = father_;
				while(fatherScope.getFather() != null)
					fatherScope = fatherScope.getFather();
				assert fatherScope instanceof MMethod;
				MClass fatherClass = ((MMethod)fatherScope).getOwner();
				type_ = fatherClass;
				var_ = new MVar(type_);
				// TODO: Too many to list out...
				return;
			case 5:
				// ArrayAllocationExpression
				type_ = SymbolTable.getType("array");
				expr_.register();
				if(expr_.getType() instanceof MInt)
					return;
				errorMsg = "The part in the array allocation expression is not an int type!";
				break;
			case 6:
				// AllocationExpression
				type_ = SymbolTable.queryClass(literal_);
				if (type_ != null)
					return;
				errorMsg = "The identifier [" + literal_ + "] in an allocation expression is not defined";
				break;
			case 7:
				// NotExpression
				type_ = SymbolTable.getType("boolean");
				expr_.register();
				if(expr_.getType() instanceof MBool)
					return;
				errorMsg = "The part in the not expression is not a boolean type";
				break;
			case 8:
				// BracketExpression
				expr_.register();
				type_ = expr_.getType();
				return;
			default:
				break;
		}
		ErrorHandler.errorPrint(errorMsg);
	}
	
	// 判断在哪个method里面
	public MMethod getMethodScope() {
		MScope father = father_;
		while(!(father instanceof MMethod)) {
			father = father.getFather();
			assert father!=null: "The father of a block is null!\n";
		}
		return (MMethod)father;
	}

	public MType getType() {
		return type_;
	}
	
	public int getWhich() {
		return which_;
	}

	public MVar getVar() {
		return var_;
	}
	
	// Only for case 5
	public int arrayLength() {
		if (expr_.getWhich() != 8) return -1;
		MPrimExpr prim_expr = expr_.getPrimExpr();
		if (prim_expr.which_ != 0) return -1;
		return prim_expr.getInteger();
	}
	
	public int getInteger() {
		if (which_ != 0) return -1;
		else return Integer.parseInt(literal_);
	}
	
	// below for piglet code generation
	// return the TEMP register that contains the primary expression value
	public String generatePigletPrimexprCode(int tab, boolean write) {
		String code = "";
		String prefixTab = "";
		for(int i = 0;i < tab; ++i)
			prefixTab += "\t";
		String returnTemp = minijava2piglet.TEMP + minijava2piglet.getTempIndex();
		String exprTemp;
		int label1, label2, label3;
		boolean isLocal = false;
		MMethod method = getMethodScope();
		if(var_ != null)
			isLocal = method.judgeLocalVar(var_);
		
		switch (which_) {
		case 0:
			// IntegerLiteral
			code += prefixTab + "MOVE " + returnTemp + " " + literal_ + "\n";
			minijava2piglet.writeCode(code);
			break;
		case 1:
			// TrueLiteral
			code += prefixTab + "MOVE " + returnTemp + " 1\n";
			minijava2piglet.writeCode(code);
			break;
		case 2:
			// FalseLiteral
			code += prefixTab + "MOVE " + returnTemp + " 0\n";
			minijava2piglet.writeCode(code);
			break;
		case 3:
			// Identifier
			if(isLocal) {
				code += prefixTab + "MOVE " + returnTemp + " " + minijava2piglet.TEMP + var_.getTempID() + "\n";
			}
			else {
				MClass owner = getMethodScope().getOwner();
				code += prefixTab + "HLOAD " + returnTemp + " TEMP 0 " + 
			            owner.queryVarOffset(var_name_) + "\n";
			}
			minijava2piglet.writeCode(code);
			break;
		case 4:
			// ThisExpression
			code += prefixTab + "MOVE " + returnTemp + " TEMP 0\n";
			minijava2piglet.writeCode(code);
			break;
		case 5:
			// ArrayAllocationExpression
			exprTemp = expr_.generatePigletExpressionCode(tab, write);
			code += prefixTab + "MOVE " + returnTemp + " HALLOCATE TIMES PLUS " + exprTemp + " 1 4\n";
			code += prefixTab + "HSTORE " + returnTemp + " 0 " + exprTemp + "\n";
			minijava2piglet.writeCode(code);
			break;
		case 6:
			// AllocationExpression
			MClass c = (MClass)type_;
			code += prefixTab + "MOVE " + returnTemp + " CALL new_" + c.getName() + "()\n";
			minijava2piglet.writeCode(code);
			var_ = new MVar(type_);
			break;
		case 7:
			// NotExpression
			exprTemp = expr_.generatePigletExpressionCode(tab, write);
			label1 = minijava2piglet.getLabelIndex();
			label2 = minijava2piglet.getLabelIndex();
			label3 = minijava2piglet.getLabelIndex();
			code += prefixTab + "CJUMP " + exprTemp + " L" + label2 + "\n";
			code += prefixTab + "L" + label1 + "\n";
			code += prefixTab + "\tMOVE " + returnTemp + " 0\n";
			code += prefixTab + "\tJUMP L" + label3 + "\n";
			code += prefixTab + "L" + label2 + "\n";
			code += prefixTab + "\tMOVE " + returnTemp + " 1\n";
			code += prefixTab + "L" + label3 + " NOOP\n";
			break;
		case 8:
			// BracketExpression
			exprTemp = expr_.generatePigletExpressionCode(tab, write);
			code += "MOVE " + returnTemp + " " + exprTemp;
			minijava2piglet.writeCode(code);
			break;
		default:
			break;
		}
		return returnTemp;
	}
}
