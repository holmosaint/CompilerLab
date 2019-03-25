package minijava.symbol;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;

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
			errorMsg = findIdentifierType();
			if(errorMsg == null)
				return;
			break;
		case 4:
			// ThisExpression
			literal_ = "this";
			MScope fatherScope = father_;
			while(fatherScope != null)
				fatherScope = fatherScope.getFather();
			assert fatherScope instanceof MMethod;
			MClass fatherClass = fatherScope.getOwner();
			type_ = fatherClass;
			return;
		case 5:
			// ArrayAllocationExpression
			expr_ = new MExpr(((ArrayAllocationExpression) prim_expr.f0.choice).f3, father_);
			type_ = new MArray();
			break;
		case 6:
			// AllocationExpression
			literal_ = ((AllocationExpression) prim_expr.f0.choice).f0.toString();
			type_ = SymbolTable.queryClass(literal);
			if(type != null)
				return;
			errorMsg = "The identifier [" + literal_ + "] in an allocation expression is not defined";
			break;
		case 7:
			// NotExpression
			expr_ = new MExpr(((NotExpression) prim_expr.f0.choice).f1, father_);
			type_ = new MBool();
			break;
		case 8:
			// BracketExpression
			expr_ = new MExpr(((NotExpression) prim_expr.f0.choice).f1, father_);
			type_ = expr_.getType();
			break;
		default:
			break;
		}
	}

	private String findIdentifierType() {
		MScope tmp_father = father_;
		while(tmp_father != null) {
			MVar v = tmp_father.queryVar(var_name);
			if(v != null) {
				type_ = v.getType();
				return null;
			}
			tmp_father = tmp_father.getFather();
		}
		assert tmp_father instanceof MMethod;
		MClass c = tmp_father.getOwner();
		while(c != null) {
			MVar v = c.queryVar(var_name);
			if(v != null) {
				type_ = v.getType();
				return null;
			}
			c = c.getFather();
		}
		String errorMsg = "Variable [" + var_name_ + "] in primary expression not found!";
		return errorMsg;
	}

	public void register() {
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
		return this.type_;
	}
}
