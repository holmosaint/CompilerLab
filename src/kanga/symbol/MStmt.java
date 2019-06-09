package kanga.symbol;

import kanga.syntaxtree.*;
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
	// 8->ALoadStmt()
	// 9->AStoreStmt()
	// 10->PassArgStmt()
	// 11->CallStmt()
	private int which_;
	private int reg_id_, reg_id2_, integer_;
	private String pre_label_ = null, label_ = null;
	private MProcedure procedure_ = null;
	private MExp exp_ = null;
	private MSimpleExp sexp_ = null;
	
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
			break;
		case 1:
			// ErrorStmt
			break;
		case 2:
			// CJumpStmt
			CJumpStmt cjump_stmt = (CJumpStmt)stmt.f0.choice;
			reg_id_ = cjump_stmt.f1.f0.which;
			label_ = cjump_stmt.f2.f0.toString();
			break;
		case 3:
			// JumpStmt
			JumpStmt jump_stmt = (JumpStmt)stmt.f0.choice;
			label_ = jump_stmt.f1.f0.toString();
			break;
		case 4:
			// HStoreStmt
			HStoreStmt hstore_stmt = (HStoreStmt)stmt.f0.choice;
			reg_id_ = hstore_stmt.f1.f0.which;
			integer_ = Integer.parseInt(hstore_stmt.f2.f0.toString());
			reg_id2_ = hstore_stmt.f3.f0.which;
			break;
		case 5:
			// HLoadStmt
			HLoadStmt hload_stmt = (HLoadStmt)stmt.f0.choice;
			reg_id_ = hload_stmt.f1.f0.which;
			reg_id2_ = hload_stmt.f2.f0.which;
			integer_ = Integer.parseInt(hload_stmt.f3.f0.toString());
			break;
		case 6:
			// MoveStmt
			MoveStmt move_stmt = (MoveStmt)stmt.f0.choice;
			reg_id_ = move_stmt.f1.f0.which;
			exp_ = new MExp(move_stmt.f2);
			break;
		case 7:
			// PrintStmt
			PrintStmt print_stmt = (PrintStmt)stmt.f0.choice;
			sexp_ = new MSimpleExp(print_stmt.f1);
			break;
		case 8:
			// ALoadStmt
			ALoadStmt aload_stmt = (ALoadStmt)stmt.f0.choice;
			reg_id_ = aload_stmt.f1.f0.which;
			integer_ = Integer.parseInt(aload_stmt.f2.f1.f0.toString());
			break;
		case 9:
			// AStoreStmt
			AStoreStmt astore_stmt = (AStoreStmt)stmt.f0.choice;
			integer_ = Integer.parseInt(astore_stmt.f1.f1.f0.toString());
			reg_id_ = astore_stmt.f2.f0.which;
			break;
		case 10:
			// PassArgStmt
			PassArgStmt pass_arg_stmt = (PassArgStmt)stmt.f0.choice;
			integer_ = Integer.parseInt(pass_arg_stmt.f1.f0.toString());
			reg_id_ = pass_arg_stmt.f2.f0.which;
			break;
		case 11:
			// CallStmt
			CallStmt call_stmt = (CallStmt)stmt.f0.choice;
			sexp_ = new MSimpleExp(call_stmt.f1);
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm");
		}
	}

	public String getLabel() {
		return pre_label_;
	}

	public String getInfo() {
		String res = "";
		if (pre_label_ != null) {
			res += pre_label_;
		}
		switch (which_) {
		case 0:
			// NoOpStmt
			res += "\tNOOP\n";
			break;
		case 1:
			// ErrorStmt
			res += "\tERROR\n";
			break;
		case 2:
			// CJumpStmt
			res += "\tCJUMP " + MProcedure.registers_[reg_id_] + " " + 
				   label_ + "\n";
			break;
		case 3:
			// JumpStmt
			res += "\tJUMP " + label_ + "\n";
			break;
		case 4:
			// HStoreStmt
			res += "\tHSTORE " + MProcedure.registers_[reg_id_] + " " + 
				   integer_ + " " + MProcedure.registers_[reg_id2_] + "\n";
			break;
		case 5:
			// HLoadStmt
			res += "\tHLOAD " + MProcedure.registers_[reg_id_] + " " + 
					MProcedure.registers_[reg_id2_] + " " + integer_ + "\n";
			break;
		case 6:
			// MoveStmt
			res += "\tMOVE " + MProcedure.registers_[reg_id_] + " " + 
					exp_.getInfo() + "\n";
			break;
		case 7:
			// PrintStmt
			res += "\tPRINT " + sexp_.getInfo() + "\n";
			break;
		case 8:
			// ALoadStmt
			res += "\tALOAD " + MProcedure.registers_[reg_id_] + 
				   " SPILLEDARG " + integer_ + "\n";
			break;
		case 9:
			// AStoreStmt
			res += "\tASTORE " + "SPILLEDARG " + integer_ + " " + 
				   MProcedure.registers_[reg_id_] + "\n";
			break;
		case 10:
			// PassArgStmt
			res += "\tPASSARG " + integer_ + " " + 
				   MProcedure.registers_[reg_id_] + "\n";
			break;
		case 11:
			// CallStmt
			res += "\tCALL " + sexp_.getInfo() + "\n";
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm");
		}
		return res;
	}

}
