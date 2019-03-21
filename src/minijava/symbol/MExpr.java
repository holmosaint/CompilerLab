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
	private int which_;
	private String op_ = null;
	private MPrimExpr prim_expr = null, prim_expr2 = null;
	private ArrayList<MExpr> exprs_ = null;
	private String var_name_ = null;
	private MVar var_;
	public MExpr() {
		
	}
	public MExpr(Expression expr) {
		
	}
}
