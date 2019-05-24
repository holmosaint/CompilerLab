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
	private String op_ = null;
	private ArrayList<Integer> tmp_ids_ = null;
	private MSimpleExp sexp_;
	public MExp(Exp exp) {
		which_ = exp.f0.which;
		switch (which_) {
		case 0:
			// Call
			// "CALL" sexp_ "TEMP" tmp_ids_
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
			// "HALLOCATE" sexp_
			HAllocate hallocate = (HAllocate)exp.f0.choice;
			sexp_ = new MSimpleExp(hallocate.f1);
			break;
		case 2:
			// BinOp
			// op_ "TEMP" tmp_id_ sexp_
			BinOp bin_op = (BinOp)exp.f0.choice;
			op_ = ((NodeToken)bin_op.f0.f0.choice).toString();
			tmp_id_ = Integer.parseInt(bin_op.f1.f1.f0.toString());
			sexp_ = new MSimpleExp(bin_op.f2);
			break;
		case 3:
			// SimpleExp
			// sexp_
			sexp_ = new MSimpleExp((SimpleExp)exp.f0.choice);
			break;
		default:
			ErrorHandler.errorPrint("emmm??");
		}
	}
	
	public ArrayList<Integer> getUsedIds() {
		ArrayList<Integer> used_ids = new ArrayList<Integer>();
		switch (which_) {
		case 0:
			// Call
			used_ids = (ArrayList<Integer>) tmp_ids_.clone();
			if (sexp_.getUsedId() != -1) used_ids.add(sexp_.getUsedId());
			break;
		case 1:
			// HAlloate
			if (sexp_.getUsedId() != -1) used_ids.add(sexp_.getUsedId());
			break;
		case 2:
			// BinOp
			used_ids.add(tmp_id_);
			if (sexp_.getUsedId() != -1) used_ids.add(sexp_.getUsedId());
			break;
		case 3:
			// SimpleExp
			if (sexp_.getUsedId() != -1) used_ids.add(sexp_.getUsedId());
			break;
		default:
			ErrorHandler.errorPrint("emmm??");
		}
		return used_ids;
	}

	public String getInfo() {
		String res = "";
		switch (which_) {
		case 0:
			// Call
			// "CALL" sexp_ tmp_ids_
			res += "CALL " + sexp_.getInfo() + "( ";
			for (int id : tmp_ids_) {
				res += "TEMP " + id + " ";
			}
			res += ")";
			break;
		case 1:
			// HAlloate
			// "HALLOCATE" sexp_
			res += "HALLOCATE " + sexp_.getInfo();
			break;
		case 2:
			// BinOp
			// op_ "TEMP" tmp_id_ sexp_
			res += op_ + " TEMP " + tmp_id_ + " " + sexp_.getInfo();
			break;
		case 3:
			// SimpleExp
			// sexp_
			res += sexp_.getInfo();
			break;
		}
		return res;
	}
}
