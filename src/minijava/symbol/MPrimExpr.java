package minijava.symbol;

import minijava.syntaxtree.*;

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
	private int which_;
	private String literal_ = null;
	private String var_name_ = null;
	private MVar var_ = null;
	private MExpr expr_ = null;
	private MType type_ = null;
	
	public MPrimExpr(PrimaryExpression prim_expr) {
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
			break;
		case 5:
			// ArrayAllocationExpression
			expr_ = new MExpr(((ArrayAllocationExpression) prim_expr.f0.choice).f3);
			break;
		case 6:
			// AllocationExpression
			literal_ = ((AllocationExpression) prim_expr.f0.choice).f0.toString();
			break;
		case 7:
			// NotExpression
			expr_ = new MExpr(((NotExpression) prim_expr.f0.choice).f1);
			break;
		case 8:
			// BracketExpression
			expr_ = new MExpr(((BracketExpression) prim_expr.f0.choice).f1);
			break;
		default:
			break;
		}
	}
}
