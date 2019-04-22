package minijava.symbol;


import minijava.syntaxtree.*;
import minijava2piglet.minijava2piglet;
import util.ErrorHandler;

import java.util.*;

public class MExpr {
	// 0->AndExpression
	// 1->CompareExpression
	// 2->PlusExpression
	// 3->MinusExpression
	// 4->TimesExpression
	// 5->ArrayLookup
	// 6->ArrayLength
	// 7->MessageSend
	// 8->PrimaryExpression
	private MScope father_;
	private int which_;
	private String op_ = null;
	private MPrimExpr prim_expr_ = null, prim_expr2_ = null;
	private ArrayList<MExpr> exprs_ = null;
	private String method_name_ = null;
	private MMethod method_ = null;
	// TODO: type_ should be assigned when checking
	private MType type_ = null;
	
	public MExpr(Expression expr, MScope father) {
		father_ = father;
		which_ = expr.f0.which;
		
		switch (which_) {
		case 0:
			// AndExpression
			op_ = "&&";
			prim_expr_ = new MPrimExpr(((AndExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((AndExpression) expr.f0.choice).f2, father_);
			break;
		case 1:
			// CompareExpression
			op_ = "<";
			prim_expr_ = new MPrimExpr(((CompareExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((CompareExpression) expr.f0.choice).f2, father_);
			break;
		case 2:
			// PlusExpression
			op_ = "+";
			prim_expr_ = new MPrimExpr(((PlusExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr((((PlusExpression) expr.f0.choice)).f2, father_);
			break;
		case 3:
			// MinusExpression
			op_ = "-";
			prim_expr_ = new MPrimExpr(((MinusExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((MinusExpression) expr.f0.choice).f2, father_);
			break;
		case 4:
			// TimesExpression
			op_ = "*";
			prim_expr_ = new MPrimExpr(((TimesExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((TimesExpression) expr.f0.choice).f2, father_);
			break;
		case 5:
			// ArrayLookup
			op_ = "[]";
			prim_expr_ = new MPrimExpr(((ArrayLookup) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((ArrayLookup) expr.f0.choice).f2, father_);
			break;
		case 6:
			// ArrayLength
			op_ = ".length";
			prim_expr_ = new MPrimExpr(((ArrayLength) expr.f0.choice).f0, father_);
			break;
		case 7:
			// MessageSend
			op_ = "message_send";
			prim_expr_ = new MPrimExpr(((MessageSend) expr.f0.choice).f0, father_);
			method_name_ = ((MessageSend) expr.f0.choice).f2.f0.toString();
			exprs_ = new ArrayList<MExpr>();
			if (((MessageSend) expr.f0.choice).f4.present()) {
				ExpressionList expr_list = ((ExpressionList) ((MessageSend) expr.f0.choice).f4.node);
				exprs_.add(new MExpr(expr_list.f0, father_));
				for (Node node : expr_list.f1.nodes) {
					ExpressionRest declare = (ExpressionRest) node;
					exprs_.add(new MExpr(declare.f1, father_));
				}
			}
			break;
		case 8:
			// PrimaryExpression
			op_ = "";
			prim_expr_ = new MPrimExpr((PrimaryExpression) expr.f0.choice, father_);
			break;
		default:
			break;
		}
	}

	void register() {
		String errorMsg = "";
		switch (which_) {
			case 0:
				// AndExpression
				// AndExpression ::= PrimaryExpression "&&" PrimaryExpression
				type_ = SymbolTable.getType("boolean");
				prim_expr_.register();
				prim_expr2_.register();
				
				if((prim_expr_.getType() instanceof MBool) && (prim_expr2_.getType() instanceof MBool))
					return;
				errorMsg = "The two parts of the Expression are not all boolean types!";
				break;
			case 1:
				// Compare Expression
				// CompareExpression ::= PrimaryExpression "<" PrimaryExpression
				type_ = SymbolTable.getType("boolean");
				prim_expr_.register();
				prim_expr2_.register();

				if((prim_expr_.getType() instanceof MInt) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The two part of the Expression are not all int type! in which " + which_;
				break;
			case 2:
			case 3:
			case 4:
				// (Plus|Minus|Times)Expression
				type_ = SymbolTable.getType("int");
				prim_expr_.register();
				prim_expr2_.register();

				if((prim_expr_.getType() instanceof MInt) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The two part of the Expression are not all int type! in which " + which_;
				break;
			case 5:
				// ArrayLookup
				// ArrayLookup ::= PrimaryExpression "[" PrimaryExpression "]"
				type_ = SymbolTable.getType("int");
				prim_expr_.register();
				prim_expr2_.register();
				if((prim_expr_.getType() instanceof MArray)) {
					if(prim_expr2_.getType() instanceof MInt) {
						// Check index
						int length, index;
						
						if (prim_expr_.getWhich() == 3) {
							length = prim_expr_.getVar().getLength();
						} else {
							assert prim_expr_.getWhich() == 5;
							length = prim_expr_.arrayLength();
						}
						index = prim_expr2_.getInteger();
						
						if (length != -1 && index != -1 && length <= index) {
							errorMsg = "Array index is wrong " + index + "/" + length;
						} else {
							return;
						}
					}
					else {
						errorMsg = "the index part of the Array Lookup Expression is not an int type!";
					}
				} else {
					errorMsg = "The first part of the Array Lookup Expression is not an array type";
				}
				break;
			case 6:
				// ArrayLength
				// ArrayLength ::= PrimaryExpression "." "length"
				type_ = SymbolTable.getType("int");
				prim_expr_.register();
				
				if((prim_expr_.getType() instanceof MArray))
					return;
				errorMsg = "The part of the ArrayLength expression is not an array type!";
				break;
			case 7:
				// MessageSend, using method
				// MessageSend ::= PrimaryExpression "." Identifier "(" ( ExpressionList )? ")"
				prim_expr_.register();
				for (MExpr expr : exprs_) {
					expr.register();
				}
				
				if (!(prim_expr_.getType() instanceof MClass)) {
					errorMsg = "Primary Expression in MessageSend should be an instance of a class";
					break;
				}
				method_ = ((MClass)prim_expr_.getType()).queryMethod(method_name_);
				if (method_ == null) {
					errorMsg = "Using undefined method " + method_name_;
					break;
				}
				
				method_.matchParam(exprs_);
				type_ = method_.getRetType();
				return;
			case 8:
				// PrimaryExpression
				prim_expr_.register();
				type_ = prim_expr_.getType();
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
	
	public MPrimExpr getPrimExpr() {
		return prim_expr_;
	}
	
	public boolean isAllocation() {
		if (which_ != 8) return false;
		return prim_expr_.getWhich() == 6;
	}
	
	public boolean isArrayAllocation() {
		if (which_ != 8) return false;
		return prim_expr_.getWhich() == 5;
	}
	
	public int arrayLength() {
		return prim_expr_.arrayLength();
	}
	
	public int getInteger() {
		return prim_expr_.getInteger();
	}
	
	// below for piglet code generation
	// return the TEMP register which the value of the expression will be stored
	public String generatePigletExpressionCode(int tab, boolean write) {
		String code = "";
		String prefixTab = "";
		for(int i = 0;i < tab; ++i)
			prefixTab += "\t";
		
		// 存表达式值的TEMP寄存器
		String returnTemp = minijava2piglet.TEMP + minijava2piglet.getTempIndex();
		String tempExpr1, tempExpr2; // 两个primexpr的返回寄存器
		ArrayList<String> tempExprs = new ArrayList<String>();
		int label1, label2;
		int methodOffset = -1;
		
		switch (which_) {
		case 0:
			// AndExpression
			label1 = minijava2piglet.getLabelIndex();
			label2 = minijava2piglet.getLabelIndex();
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write);
			tempExpr2 = prim_expr2_.generatePigletPrimexprCode(tab, write);
			code += prefixTab + "MOVE " + returnTemp + " 1\n";
			code += prefixTab + "CJUMP LT 0 " + tempExpr1 + " L" + label1 + "\n";
			code += prefixTab + "\tCJUMP LT 0 " + tempExpr2 + " L" + label1 + "\n";
			code += prefixTab + "\t\tJUMP L" + label2 + "\n";
			code += prefixTab + "L " + label1 + "\n";
			code += prefixTab + "\tMOVE " + returnTemp + " 0\n";
			code += prefixTab + "L " + label2 + " NOOP\n";
			minijava2piglet.writeCode(code);
			break;
		case 1:
			// CompareExpression
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write);
			tempExpr2 = prim_expr2_.generatePigletPrimexprCode(tab, write);
			code += prefixTab + "MOVE " + returnTemp + " LT " + tempExpr1 + " " + tempExpr2 + "\n";
			minijava2piglet.writeCode(code);
			break;
		case 2:
			// PlusExpression
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write);
			tempExpr2 = prim_expr2_.generatePigletPrimexprCode(tab, write);
			code += prefixTab + "MOVE " + returnTemp + " PLUS " + tempExpr1 + " " + tempExpr2 + "\n";
			minijava2piglet.writeCode(code);
			break;
		case 3:
			// MinusExpression
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write);
			tempExpr2 = prim_expr2_.generatePigletPrimexprCode(tab, write);
			code += prefixTab + "MOVE " + returnTemp + " MINUS " + tempExpr1 + " " + tempExpr2 + "\n";
			minijava2piglet.writeCode(code);
			break;
		case 4:
			// TimesExpression
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write);
			tempExpr2 = prim_expr2_.generatePigletPrimexprCode(tab, write);
			code += prefixTab + "MOVE " + returnTemp + " TIMES " + tempExpr1 + " " + tempExpr2 + "\n";
			minijava2piglet.writeCode(code);
			break;
		case 5:
			// ArrayLookup
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write); // base address
			tempExpr2 = prim_expr2_.generatePigletPrimexprCode(tab, write);
			code += prefixTab + "HLOAD " + returnTemp + " " + tempExpr1 + " TIMES PLUS " + tempExpr2 + " 1 4\n";
			minijava2piglet.writeCode(code);
			break;
		case 6:
			// ArrayLength
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write); // base address
			code += prefixTab + "MOVE " + returnTemp + " " + tempExpr1 + " 0\n";
			minijava2piglet.writeCode(code);
			break;
		case 7:
			// MessageSend			
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write); // base address and the first parameter
			for(MExpr e : exprs_) {
				tempExprs.add(e.generatePigletExpressionCode(tab, write));
			}
			methodOffset = prim_expr_.getVar().getOwner().queryMethodOffset(method_name_); // 获得偏移量，保证是4的倍数
			
			String midTemp = minijava2piglet.TEMP + minijava2piglet.getTempIndex();
			code += prefixTab + "HLOAD " + midTemp + " " + tempExpr1 + " 0\n"; // 获得DTable的基址
			code += prefixTab + "HLOAD " + midTemp + " " + midTemp + " " + methodOffset + "\n";
			code += prefixTab + "MOVE " + returnTemp + " CALL " + midTemp + " ( ";
			for(String s : tempExprs) {
				code += s + " ";
			}
			code += ")\n";
			minijava2piglet.writeCode(code);
			break;
		case 8:
			// PrimaryExpression
			tempExpr1 = prim_expr_.generatePigletPrimexprCode(tab, write);
			code += prefixTab + "MOVE " + returnTemp + " " + tempExpr1 + "\n";
			minijava2piglet.writeCode(code);
			break;
		default:
			break;
		}
		
		return returnTemp;
	}
}
