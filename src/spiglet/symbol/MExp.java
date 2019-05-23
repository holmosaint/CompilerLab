package spiglet.symbol;

import java.util.ArrayList;
import util.ErrorHandler;
import spiglet.syntaxtree.*;

public class MExp {
	// 0->Call
	// 1->HAllocate
	// 2->BinOp
	// 3->SimpleExp
	private int which_, tmp_id_;
	private String op_;
	private ArrayList<Integer> tmp_ids_ = null;
	private MSimpleExp sexp_;
	public MExp(Exp exp) {
		which_ = exp.f0.which;
		switch (which_) {
		case 0:
			// Call
			Call call = (Call)exp.f0.choice;
			sexp_ = new MSimpleExp(call.f1);
			tmp_ids_ = new ArrayList<Integer>();
			for (Node node : call.f3.nodes) {
				Temp temp = (Temp)node;
				tmp_ids_.add(Integer.parseInt(temp.f1.f0.toString()));
			}
			break;
		case 1:
			// HAlloate
			HAllocate hallocate = (HAllocate)exp.f0.choice;
			sexp_ = new MSimpleExp(hallocate.f1);
			break;
		case 2:
			// BinOp
			BinOp bin_op = (BinOp)exp.f0.choice;
			op_ = ((NodeToken)bin_op.f0.f0.choice).toString();
			tmp_id_ = Integer.parseInt(bin_op.f1.f1.f0.toString());
			sexp_ = new MSimpleExp(bin_op.f2);
			break;
		case 3:
			// SimpleExp
			sexp_ = new MSimpleExp((SimpleExp)exp.f0.choice);
			break;
		default:
			ErrorHandler.errorPrint("emmm??");
		}
	}

}
