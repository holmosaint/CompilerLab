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
			type_ = new MInt();
			break;
		case 1:
			// TrueLiteral
			literal_ = "true";
			type_ = new MBool();
			break;
		case 2:
			// FalseLiteral
			literal_ = "false";
			type_ = new MBool();
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
			expr_ = new MExpr(((ArrayAllocationExpression) prim_expr.f0.choice).f3, father_);
			type_ = new MArray();
			break;
		case 6:
			// AllocationExpression
			literal_ = ((AllocationExpression) prim_expr.f0.choice).f0.toString();

			break;
		case 7:
			// NotExpression
			expr_ = new MExpr(((NotExpression) prim_expr.f0.choice).f1, father_);
			type_ = new MBool();
			break;
		case 8:
			// BracketExpression
			expr_ = new MExpr(((BracketExpression) prim_expr.f0.choice).f1, father_);
			type_ = expr_.getType();
			break;
		default:
			break;
		}
	}

	void register() {
		String errorMsg = "";
		switch (which_) {
			case 0:
				// IntegerLiteral
				return;
			case 1:
				// TrueLiteral
				return;
			case 2:
				// FalseLiteral
				return;
			case 3:
				// Identifier
				// TODO: find whether the Identifier has been defined.
				return;
			case 4:
				// ThisExpression
				return;
			case 5:
				// ArrayAllocationExpression
				if(expr_.getType() instanceof MInt)
					return;
				errorMsg = "The part in the array allocation expression is not an int type!";
				break;
			case 6:
				// AllocationExpression
				// TODOs
				break;
			case 7:
				// NotExpression
				if(expr_.getType() instanceof MBool)
					return;
				errorMsg = "The part in the not expression is not a boolean type";
				break;
			case 8:
				// BracketExpression
				return;
			default:
				break;
		}
		System.out.println(errorMsg);
		System.exit(1);
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
