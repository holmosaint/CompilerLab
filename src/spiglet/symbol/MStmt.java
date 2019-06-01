package spiglet.symbol;

import java.util.*;

import spiglet.syntaxtree.*;
import util.ErrorHandler;

public class MStmt {
	// 0->NoOpStmt()
	// 1->ErrorStmt()
	// 2->CJumpStmt()
	// 3->JumpStmt()
	// 4->HStoreStmt()
	// 5->HLoadStmt()
	// 6->MoveStmt()
	// 7->PrintStmt()
	private int which_;
	private int tmp_id_, tmp_id2_, integer_;
	private String pre_label_ = null, label_ = null;
	private MExp exp_ = null;
	private MSimpleExp sexp_ = null;
	private MProcedure procedure_ = null;
	private HashSet<Integer> used_ids_ = null;
	private HashSet<Integer> defined_ids_ = null;
	
	public MStmt(NodeSequence node_seq, MProcedure procedure) {
		procedure_ = procedure;

		NodeOptional label = (NodeOptional)(node_seq.elementAt(0));
		if (label.present()) {
			pre_label_ = ((Label)label.node).f0.toString();
		}
		Stmt stmt = (Stmt)node_seq.elementAt(1);
		which_ = stmt.f0.which;
		switch (which_) {
		case 0:
			// NoOpStmt
			// "NOOP"
			break;
		case 1:
			// ErrorStmt
			// "ERROR"
			break;
		case 2:
			// CJumpStmt
			// "CJUMP" "TEMP" tmp_id_ label_
			CJumpStmt cjump_stmt = (CJumpStmt)stmt.f0.choice;
			tmp_id_ = Integer.parseInt(cjump_stmt.f1.f1.f0.toString());
			label_ = cjump_stmt.f2.f0.toString();
			break;
		case 3:
			// JumpStmt
			// "JUMP" label_
			JumpStmt jump_stmt = (JumpStmt)stmt.f0.choice;
			label_ = jump_stmt.f1.f0.toString();
			break;
		case 4:
			// HStoreStmt
			// "HSTORE" "TEMP" tmp_id_ integer "TEMP" tmp_id2_
			HStoreStmt hstore_stmt = (HStoreStmt)stmt.f0.choice;
			tmp_id_ = Integer.parseInt(hstore_stmt.f1.f1.f0.toString());
			integer_ = Integer.parseInt(hstore_stmt.f2.f0.toString());
			tmp_id2_ = Integer.parseInt(hstore_stmt.f3.f1.f0.toString());
			break;
		case 5:
			// HLoadStmt
			// "HLOAD" "TEMP" tmp_id_ "TEMP" tmp_id2_ integer_
			HLoadStmt hload_stmt = (HLoadStmt)stmt.f0.choice;
			tmp_id_ = Integer.parseInt(hload_stmt.f1.f1.f0.toString());
			tmp_id2_ = Integer.parseInt(hload_stmt.f2.f1.f0.toString());
			integer_ = Integer.parseInt(hload_stmt.f3.f0.toString());
			break;
		case 6:
			// MoveStmt
			// "MOVE" "TEMP" tmp_id_ exp_
			MoveStmt move_stmt = (MoveStmt)stmt.f0.choice;
			tmp_id_ = Integer.parseInt(move_stmt.f1.f1.f0.toString());
			exp_ = new MExp(move_stmt.f2);
			break;
		case 7:
			// PrintStmt
			// "PRINT" sexp_
			PrintStmt print_stmt = (PrintStmt)stmt.f0.choice;
			sexp_ = new MSimpleExp(print_stmt.f1);
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm!!!");
		}
	}

	public HashSet<Integer> getDefinedIds() {
		if (defined_ids_ != null) return defined_ids_;
		defined_ids_ = new HashSet<Integer>();
		if (which_ == 5 || which_ == 6) {
			if (!getUsedIds().contains(tmp_id_)) {
				defined_ids_.add(tmp_id_);				
			}
		}
		return defined_ids_;
	}
	
	public HashSet<Integer> getUsedIds() {
		if (used_ids_ != null) return used_ids_;
		used_ids_ = new HashSet<Integer>();
		switch (which_) {
		case 0:
			// NoOpStmt
			break;
		case 1:
			// ErrorStmt
			break;
		case 2:
			// CJumpStmt
			used_ids_.add(tmp_id_);
			break;
		case 3:
			// JumpStmt
			break;
		case 4:
			// HStoreStmt
			used_ids_.add(tmp_id_);
			used_ids_.add(tmp_id2_);
			break;
		case 5:
			// HLoadStmt
			used_ids_.add(tmp_id2_);
			break;
		case 6:
			// MoveStmt
			used_ids_ = exp_.getUsedIds();
			break;
		case 7:
			// PrintStmt
			if (sexp_.getUsedId() != -1) used_ids_.add(sexp_.getUsedId());
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm!!!");
		}
		return used_ids_;
	}
	
	public String getLabel() {
		return pre_label_;
	}
	
	public boolean isJump() {
		return which_ == 3;
	}

	public String getExtraSuccessor() {
		if (which_ == 2 || which_ == 3) return label_;
		else return null;
	}
	
	// For debugging
	String[] names = {"NOOP", "ERROR", "CJUMP", "JUMP", "HSTORE", "HLOAD", "MOVE", "PRINT"};
	public String getName() {
		String res = names[which_];
		return res;
	}
	
	public int getParamNum() {
		int res = 0;
		if (exp_ != null) res = exp_.getParamNum();
		return res;
	}
	
	public String toKanga(HashMap<Integer, Integer> tmp2reg,
			  			   HashSet<Integer> OUTs, int stack_num) {
		String res = "", reg_name = "", reg2_name = "";
		if (pre_label_ != null) res += pre_label_;
		switch (which_) {
		case 0:
			// NoOpStmt
			res += "\t" + getName();
			break;
		case 1:
			// ErrorStmt
			res += "\t" + getName();
			break;
		case 2:
			// CJumpStmt
			// "CJUMP" "TEMP" tmp_id_ label_
			if (tmp2reg.get(tmp_id_) < 0) {
				res += "\tALOAD t7 SPILLEDARG " + (stack_num + tmp2reg.get(tmp_id_));
				reg_name = "t7";

			} else {
				reg_name = MProcedure.registers_[tmp2reg.get(tmp_id_)];
			}
			res += "\t" + getName() + " " + reg_name + " " + label_;
			break;
		case 3:
			// JumpStmt
			// "JUMP" label_
			res += "\t" + getName() + " " + label_;
			break;
		case 4:
			// HStoreStmt
			// "HSTORE" "TEMP" tmp_id_ integer_ "TEMP" tmp_id2_
			if (tmp2reg.get(tmp_id_) < 0) {
				res += "\tALOAD t7 SPILLEDARG " + (stack_num + tmp2reg.get(tmp_id_)) + "\n";
				reg_name = "t7";
			} else {
				reg_name = MProcedure.registers_[tmp2reg.get(tmp_id_)];
			}
			if (tmp2reg.get(tmp_id2_) < 0) {
				res += "\tALOAD t8 SPILLEDARG " + (stack_num + tmp2reg.get(tmp_id2_)) + "\n";
				reg2_name = "t8";
			} else {
				reg2_name = MProcedure.registers_[tmp2reg.get(tmp_id2_)];
			}
			res += "\t" + getName() + " " + reg_name + " " + integer_ + 
				   " " + reg2_name;
			break;
		case 5:
			// HLoadStmt
			// "HLOAD" "TEMP" tmp_id_ "TEMP" tmp_id2_ integer_			
			if (tmp2reg.containsKey(tmp_id_) && tmp2reg.containsKey(tmp_id2_) &&
				OUTs.contains(tmp_id_)) {
				if (tmp2reg.get(tmp_id_) < 0) {
					reg_name = "t7";
				} else {
					reg_name = MProcedure.registers_[tmp2reg.get(tmp_id_)];
				}
				if (tmp2reg.get(tmp_id2_) < 0) {
					res += "\tALOAD t8 SPILLEDARG " + (stack_num + tmp2reg.get(tmp_id2_)) + "\n";
					reg2_name = "t8";
				} else {
					reg2_name = MProcedure.registers_[tmp2reg.get(tmp_id2_)];
				}
				
				res += "\t" + getName() + " " + reg_name +
					   " " + reg2_name + " " + integer_;
				if (tmp2reg.get(tmp_id_) < 0) {
					res += "\n\tASTORE SPILLEDARG " + (stack_num + tmp2reg.get(tmp_id_)) + " t7";
				}
			}
			else {
				res += "\tNOOP";
			}
			break;
		case 6:
			// MoveStmt
			// "MOVE" "TEMP" tmp_id_ exp_
			if (tmp2reg.containsKey(tmp_id_) && OUTs.contains(tmp_id_)) {
				if (tmp2reg.get(tmp_id_) < 0) {
					reg_name = "t7";
				} else {
					reg_name = MProcedure.registers_[tmp2reg.get(tmp_id_)];
				}
				res += exp_.getCall(tmp2reg, stack_num);
				res += exp_.prepare(tmp2reg, stack_num);
				res += "\t" + getName() + " " + reg_name + 
					   " " + exp_.toKanga(tmp2reg);
				if (tmp2reg.get(tmp_id_) < 0) {
					res += "\n\tASTORE SPILLEDARG " + (stack_num + tmp2reg.get(tmp_id_)) + " t7";
				}
			}
			else {
				res += "\tNOOP";
			}
			break;
		case 7:
			// PrintStmt
			// "PRINT" sexp_
			res += sexp_.prepare(tmp2reg, stack_num);
			res += "\t" + getName() + " " + sexp_.toKanga(tmp2reg);
			break;
		}
		res += "\n";
		return res;
	}
	
	// For debugging
	public String getInfo(HashMap<Integer, Integer> tmp2reg,
						  HashSet<Integer> OUTs) {
		String res = "";
		if (pre_label_ != null) res += pre_label_;
		res += "\t";
		switch (which_) {
		case 0:
			// NoOpStmt
			res += getName();
			break;
		case 1:
			// ErrorStmt
			res += getName();
			break;
		case 2:
			// CJumpStmt
			// "CJUMP" "TEMP" tmp_id_ label_
			res += getName() + " TEMP " + tmp2reg.get(tmp_id_) + " " + label_;
			break;
		case 3:
			// JumpStmt
			// "JUMP" label_
			res += getName() + " " + label_;
			break;
		case 4:
			// HStoreStmt
			// "HSTORE" "TEMP" tmp_id_ integer_ "TEMP" tmp_id2_
			res += getName() + " TEMP " + tmp2reg.get(tmp_id_) + " " + 
				   integer_ + " TEMP " + tmp2reg.get(tmp_id2_);
			break;
		case 5:
			// HLoadStmt
			// "HLOAD" "TEMP" tmp_id_ "TEMP" tmp_id2_ integer_
			if (tmp2reg.containsKey(tmp_id_) && tmp2reg.containsKey(tmp_id2_) &&
				OUTs.contains(tmp_id_))
				res += getName() + " TEMP " + tmp2reg.get(tmp_id_) + " TEMP " + 
					   tmp2reg.get(tmp_id2_) + " " + integer_;
			else
				res += "NOOP";
			break;
		case 6:
			// MoveStmt
			// "MOVE" "TEMP" tmp_id_ exp_
			if (tmp2reg.containsKey(tmp_id_) && OUTs.contains(tmp_id_))
				res += getName() + " TEMP " + tmp2reg.get(tmp_id_) + " " + 
					   exp_.getInfo(tmp2reg);
			else
				res += "NOOP";
			break;
		case 7:
			// PrintStmt
			// "PRINT" sexp_
			res += getName() + " " + sexp_.getInfo(tmp2reg);
			break;
		}
		res += "\n";
		return res;
	}
}
