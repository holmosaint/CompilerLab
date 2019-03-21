package minijava.symbol;

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
	private String literal_;
	private String var_name_;
	private MVar var_;
	private MExpr expr_;
}
