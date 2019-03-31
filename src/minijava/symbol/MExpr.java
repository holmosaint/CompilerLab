package minijava.symbol;


import minijava.syntaxtree.*;
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
	private String var_name_ = null;
	private MVar var_ = null;
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
				type_ = new MBool();
				prim_expr_.register();
				prim_expr2_.register();
				
				if((prim_expr_.getType() instanceof MBool) && (prim_expr2_.getType() instanceof MBool))
					return;
				errorMsg = "The two parts of the Expression are not all boolean types!";
				break;
			case 1:
				// Compare Expression
				// CompareExpression ::= PrimaryExpression "<" PrimaryExpression
				type_ = new MBool();
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
				type_ = new MInt();
				prim_expr_.register();
				prim_expr2_.register();

				if((prim_expr_.getType() instanceof MInt) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The two part of the Expression are not all int type! in which " + which_;
				break;
			case 5:
				// ArrayLookup
				// ArrayLookup ::= PrimaryExpression "[" PrimaryExpression "]"
				type_ = new MInt();
				prim_expr_.register();
				prim_expr2_.register();
				if((prim_expr_.getType() instanceof MArray)) {
					if(prim_expr2_.getType() instanceof MInt) {
						return;
					}
					else {
						errorMsg = "the index part of the Array Lookup Expression is not an int type!";
					}
				}
				else {
					errorMsg = "The first part of the Array Lookup Expression is not an array type";
				}
				break;
			case 6:
				// ArrayLength
				// ArrayLength ::= PrimaryExpression "." "length"
				type_ = new MInt();
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
}
