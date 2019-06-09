package kanga.symbol;

import kanga.syntaxtree.*;
import util.ErrorHandler;

public class MSimpleExp {
	// 0->Reg
	// 1->IntegerLiteral
	// 2->Label
	private int which_, reg_id_, integer_;
	private String label_;
	
	public MSimpleExp(SimpleExp simple_exp) {
		which_ = simple_exp.f0.which;
		switch (which_) {
		case 0:
			// Reg
			Reg reg = (Reg)simple_exp.f0.choice;
			reg_id_ = reg.f0.which;
			break;
		case 1:
			// IntegerLiteral
			IntegerLiteral integer_literal = (IntegerLiteral)simple_exp.f0.choice;
			integer_ = Integer.parseInt(integer_literal.f0.toString());
			break;
		case 2:
			// Label
			Label label = (Label)simple_exp.f0.choice;
			label_ = label.f0.toString();
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm");
		}
	}

	public String getInfo() {
		String res = "";
		switch (which_) {
		case 0:
			// Reg
			res += MProcedure.registers_[reg_id_];
			break;
		case 1:
			// IntegerLiteral
			res += integer_;
			break;
		case 2:
			// Label
			res += label_;
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm");
		}
		return res;
	}
	
}
