package spiglet.symbol;

import java.util.*;
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
	
	public HashSet<Integer> getUsedIds() {
		HashSet<Integer> used_ids = new HashSet<Integer>();
		switch (which_) {
		case 0:
			// Call
			for (Integer i : tmp_ids_) {
				used_ids.add(i);
			}
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

	public int getParamNum() {
		int res = 0;
		if (tmp_ids_ != null) res = tmp_ids_.size();
		return res;
	}

	public String getCall(HashMap<Integer, Integer> tmp2reg, int stack_num) {
		String res = "";
		if (which_ == 0) {
			for (int i = 0; i < tmp_ids_.size() && i < 4; i++) {
				if (tmp2reg.get(tmp_ids_.get(i)) < 0) {
					res += "\tALOAD " + MProcedure.registers_[19 + i] + " SPILLEDARG " + 
						   (stack_num + tmp2reg.get(tmp_ids_.get(i))) + "\n";
				}
				else {
					res += "\tMOVE " + MProcedure.registers_[19 + i] + " " +
					       MProcedure.registers_[tmp2reg.get(tmp_ids_.get(i))] + "\n";
				}
			}
			for (int i = 4; i < tmp_ids_.size(); i++) {
				if (tmp2reg.get(tmp_ids_.get(i)) < 0) {
					res += "\tALOAD t7 SPILLEDARG " + 
							   (stack_num + tmp2reg.get(tmp_ids_.get(i))) + "\n";
					res += "\tPASSARG " + (i - 3) + " t7\n";
				}
				else {
					res += "\tPASSARG " + (i - 3) + " " + 
						   MProcedure.registers_[tmp2reg.get(tmp_ids_.get(i))] + "\n";
				}
			}
			res += sexp_.prepare(tmp2reg, stack_num);
			res += "\tCALL " + sexp_.toKanga(tmp2reg) + "\n";
		}
		return res;
	}
	
	public String prepare(HashMap<Integer, Integer> tmp2reg, int stack_num) {
		String res = "";
		if (sexp_ != null) res += sexp_.prepare(tmp2reg, stack_num);
		if (which_ == 2 && tmp2reg.get(tmp_id_) < 0) {
			res += "\tALOAD t8 SPILLEDARG " + (stack_num + tmp2reg.get(tmp_id_)) + "\n";
		}
		return res;
	}
	
	public String toKanga(HashMap<Integer, Integer> tmp2reg) {
		String res = "";
		switch (which_) {
		case 0:
			// Call
			// "CALL" sexp_ tmp_ids_
			res += "v0";
			break;
		case 1:
			// HAlloate
			// "HALLOCATE" sexp_
			res += "HALLOCATE " + sexp_.toKanga(tmp2reg);
			break;
		case 2:
			// BinOp
			// op_ "TEMP" tmp_id_ sexp_
			if (tmp2reg.get(tmp_id_) < 0) {
				res += op_ + " t8 " + sexp_.toKanga(tmp2reg);
			} else {
				res += op_ + " " + MProcedure.registers_[tmp2reg.get(tmp_id_)] + " " +
					   sexp_.toKanga(tmp2reg);	
			}

			break;
		case 3:
			// SimpleExp
			// sexp_
			res += sexp_.toKanga(tmp2reg);
			break;
		}
		return res;
	}
	
	// For debugging
	public String getInfo(HashMap<Integer, Integer> tmp2reg) {
		String res = "";
		switch (which_) {
		case 0:
			// Call
			// "CALL" sexp_ tmp_ids_
			res += "CALL " + sexp_.getInfo(tmp2reg) + "( ";
			for (int id : tmp_ids_) {
				res += "TEMP " + tmp2reg.get(id) + " ";
			}
			res += ")";
			break;
		case 1:
			// HAlloate
			// "HALLOCATE" sexp_
			res += "HALLOCATE " + sexp_.getInfo(tmp2reg);
			break;
		case 2:
			// BinOp
			// op_ "TEMP" tmp_id_ sexp_
			res += op_ + " TEMP " + tmp2reg.get(tmp_id_) + " " + sexp_.getInfo(tmp2reg);
			break;
		case 3:
			// SimpleExp
			// sexp_
			res += sexp_.getInfo(tmp2reg);
			break;
		}
		return res;
	}
}
