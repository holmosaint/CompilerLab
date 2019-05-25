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
		if (which_ == 5 || which_ == 6) defined_ids_.add(tmp_id_);
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
	
	public MStmt constructChain() {
		if (which_ == 2 || which_ == 3) {
			return procedure_.getStmtByLabel(label_);
		}
		return null;
	}
	
	// For debugging
	String[] names = {"NOOP", "ERROR", "CJUMP", "JUMP", "HSTORE", "HLOAD", "MOVE", "PRINT"};
	public String getName() {
		String res = "";
		if (pre_label_ != null) res += pre_label_ + " ";
		res += names[which_];
		return res;
	}
	
	// for debugging
	public String getInfo() {
		String res = "";
		res += getName();
		switch (which_) {
		case 2:
			// CJumpStmt
			// "CJUMP" "TEMP" tmp_id_ label_
			res += " TEMP " + tmp_id_ + " " + label_;
			break;
		case 3:
			// JumpStmt
			// "JUMP" label_
			res += " " + label_;
			break;
		case 4:
			// HStoreStmt
			// "HSTORE" "TEMP" tmp_id_ integer_ "TEMP" tmp_id2_
			res += " TEMP " + tmp_id_ + " " + integer_ + " TEMP " + tmp_id2_;
			break;
		case 5:
			// HLoadStmt
			// "HLOAD" "TEMP" tmp_id_ "TEMP" tmp_id2_ integer_
			res += " TEMP " + tmp_id_ + " TEMP " + tmp_id2_ + " " + integer_;
			break;
		case 6:
			// MoveStmt
			// "MOVE" "TEMP" tmp_id_ exp_
			res += " TEMP " + tmp_id_ + " " + exp_.getInfo();
			break;
		case 7:
			// PrintStmt
			// "PRINT" sexp_
			res += " " + sexp_.getInfo();
			break;
		}
		res += "\n";
		return res;
	}
}
