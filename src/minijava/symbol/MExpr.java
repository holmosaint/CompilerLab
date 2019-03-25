package minijava.symbol;


import minijava.syntaxtree.*;
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
			type_ = new MBool();
			break;
		case 1:
			// CompareExpression
			op_ = "<";
			prim_expr_ = new MPrimExpr(((CompareExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((CompareExpression) expr.f0.choice).f2, father_);
			type_ = new MBool();
			break;
		case 2:
			// PlusExpression
			op_ = "+";
			prim_expr_ = new MPrimExpr(((PlusExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr((((PlusExpression) expr.f0.choice)).f2, father_);
			type_ = new MInt();
			break;
		case 3:
			// MinusExpression
			op_ = "-";
			prim_expr_ = new MPrimExpr(((MinusExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((MinusExpression) expr.f0.choice).f2, father_);
			type_ = new MInt();
			break;
		case 4:
			// TimesExpression
			op_ = "*";
			prim_expr_ = new MPrimExpr(((TimesExpression) expr.f0.choice).f0, father_);
			prim_expr2_ = new MPrimExpr(((TimesExpression) expr.f0.choice).f2, father_);
			type_ = new MInt();
			break;
		case 5:
			// ArrayLookup
			op_ = "[]";
			prim_expr_ = new MPrimExpr(((ArrayLookup) expr.f0.choice).f0, father_);
			prim_expr_ = new MPrimExpr(((ArrayLookup) expr.f0.choice).f2, father_);
			type_ = new MInt();
			break;
		case 6:
			// ArrayLength
			op_ = ".length";
			prim_expr_ = new MPrimExpr(((ArrayLength) expr.f0.choice).f0, father_);
			type_ = new MInt();
			break;
		case 7:
			// MessageSend
			op_ = "message_send";
			prim_expr_ = new MPrimExpr(((MessageSend) expr.f0.choice).f0, father_);
			var_name_ = ((MessageSend) expr.f0.choice).f2.f0.toString();
			if (((MessageSend) expr.f0.choice).f4.present()) {
				ExpressionList expr_list = ((ExpressionList) ((MessageSend) expr.f0.choice).f4.node);
				exprs_ = new ArrayList<MExpr>();
				exprs_.add(new MExpr(expr_list.f0, father_));
				for (Node node : expr_list.f1.nodes) {
					ExpressionRest declare = (ExpressionRest) node;
					exprs_.add(new MExpr(declare.f1, father_));
				}
			}
			// TODO: Don't know what the expression is...
			break;
		case 8:
			// PrimaryExpression
			op_ = "";
			prim_expr_ = new MPrimExpr((PrimaryExpression) expr.f0.choice, father_);
			type = prim_expr_.getType();
			break;
		default:
			break;
		}
	}

	void register() {
		String errorMsg;
		switch (which_) {
			case 0:
				// AndExpression
				if((prim_expr_.getType() instanceof MBool) && (prim_expr2_.getType() instanceof MBool))
					return;
				errorMsg = "The two part of the And Expression are not all boolean type!";
				break;
			case 1:
				// CompareExpression
				if((prim_expr_.getType() instanceof MInt) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The two part of the Compare Expression are not all int type!";
				break;
			case 2:
				// PlusExpression
				if((prim_expr_.getType() instanceof MInt) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The two part of the Plus Expression are not all int type!";
				break;
			case 3:
				// MinusExpression
				if((prim_expr_.getType() instanceof MInt) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The two part of the Minus Expression are not all int type!";
				break;
			case 4:
				// TimesExpression
				if((prim_expr_.getType() instanceof MInt) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The two part of the Times Expression are not all int type!";
				break;
			case 5:
				// ArrayLookup
				if((prim_expr_.getType() instanceof MArray) && (prim_expr2_.getType() instanceof MInt))
					return;
				errorMsg = "The first part of the Array Lookup Expression is not a array type or " + 
						   "the second part of the Array Lookup Expression is not a int type!";
				break;
			case 6:
				// ArrayLength
				if((prim_expr_.getType() instanceof MArray))
					return;
				errorMsg = "The part of the Plus Expression is not a array type!";
				break;
			case 7:
				// MessageSend
				op_ = "message_send";
				prim_expr_ = new MPrimExpr(((MessageSend) expr.f0.choice).f0);
				var_name_ = ((MessageSend) expr.f0.choice).f2.f0.toString();
				if (((MessageSend) expr.f0.choice).f4.present()) {
					ExpressionList expr_list = ((ExpressionList) ((MessageSend) expr.f0.choice).f4.node);
					exprs_ = new ArrayList<MExpr>();
					exprs_.add(new MExpr(expr_list.f0));
					for (Node node : expr_list.f1.nodes) {
						ExpressionRest declare = (ExpressionRest) node;
						exprs_.add(new MExpr(declare.f1));
					}
				}
				// TODO: Don't know what the expression is...
				break;
			case 8:
				// PrimaryExpression
				prim_expr_.register();
				return;
			default:
				break;
			}
		System.out.println(errorMsg);
		System.exit(1);
	}

	MType getType() {
		return this.type_;
	}
}
