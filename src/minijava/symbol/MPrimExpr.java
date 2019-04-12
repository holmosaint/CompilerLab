package minijava.symbol;

import minijava.syntaxtree.*;
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
	private String var_name_ = null;
	private MVar var_ = null;
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

	/*
	private String findIdentifierType() {
		MScope tmp_father = father_;
		while(tmp_father != null) {
			MVar v = tmp_father.queryVar(var_name_);
			if(v != null) {
				type_ = v.getType();
				return null;
			}
			tmp_father = tmp_father.getFather();
		}
		assert tmp_father instanceof MMethod;
		MClass c = ((MMethod)tmp_father).getOwner();
		while(c != null) {
			MVar v = c.queryVar(var_name_);
			if(v != null) {
				type_ = v.getType();
				return null;
			}
			c = c.getFather();
		}
		String errorMsg = "Variable [" + var_name_ + "] in primary expression not found!";
		return errorMsg;
	}
	*/

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
					errorMsg = "Using undefined variable";
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
				var_ = new MVar(fatherClass);
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
				var_ = new MVar(type_);
				if(type_ != null)
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

	public MType getType() {
		return type_;
	}
	
	public int getWhich() {
		return which_;
	}
	
	public MVar getVar() {
		return var_;
	}
}
