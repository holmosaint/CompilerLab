package kanga.symbol;

import kanga.syntaxtree.*;
import util.ErrorHandler;

public class MExp {
	// 0->HAllocate
	// 1->BinOp
	// 2->SimpleExp
	private int which_, reg_id_;
	private MSimpleExp sexp_ = null;
	private String ops_[] = {"LT", "PLUS", "MINUS", "TIMES"};
	private int op_;  // 0->LT, 1->PLUS, 2->MINUS, 3->TIMES
	
	public MExp(Exp exp) {
		which_ = exp.f0.which;
		switch (which_) {
		case 0:
			// HAllocate
			HAllocate hallocate = (HAllocate)exp.f0.choice;
			sexp_ = new MSimpleExp(hallocate.f1);
			break;
		case 1:
			// BinOp
			BinOp bin_op = (BinOp)exp.f0.choice;
			op_ = bin_op.f0.f0.which;
			reg_id_ = bin_op.f1.f0.which;
			sexp_ = new MSimpleExp(bin_op.f2);
			break;
		case 2:
			// SimpleExp
			SimpleExp simple_exp = (SimpleExp)exp.f0.choice;
			sexp_ = new MSimpleExp(simple_exp);
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm");
		}
	}

	public String getInfo() {
		String res = "";
		switch (which_) {
		case 0:
			// HAllocate
			res += "HALLOCATE " + sexp_.getInfo();
			break;
		case 1:
			// BinOp
			res += ops_[op_] + " " + MProcedure.registers_[reg_id_] + " " + 
				   sexp_.getInfo();
			break;
		case 2:
			// SimpleExp
			res += sexp_.getInfo();
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm");
		}
		return res;
	}

}
