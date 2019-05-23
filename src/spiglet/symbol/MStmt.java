package spiglet.symbol;

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
	
	public MStmt(NodeSequence node_seq) {
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
			tmp_id_ = Integer.parseInt(cjump_stmt.f1.f1.f0.toString());
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
			tmp_id_ = Integer.parseInt(hstore_stmt.f1.f1.f0.toString());
			integer_ = Integer.parseInt(hstore_stmt.f2.f0.toString());
			tmp_id2_ = Integer.parseInt(hstore_stmt.f3.f1.f0.toString());
			break;
		case 5:
			// HLoadStmt
			HLoadStmt hload_stmt = (HLoadStmt)stmt.f0.choice;
			tmp_id_ = Integer.parseInt(hload_stmt.f1.f1.f0.toString());
			tmp_id2_ = Integer.parseInt(hload_stmt.f2.f1.f0.toString());
			integer_ = Integer.parseInt(hload_stmt.f3.f0.toString());
			break;
		case 6:
			// MoveStmt
			MoveStmt move_stmt = (MoveStmt)stmt.f0.choice;
			tmp_id_ = Integer.parseInt(move_stmt.f1.f1.f0.toString());
			exp_ = new MExp(move_stmt.f2);
			break;
		case 7:
			// PrintStmt
			PrintStmt print_stmt = (PrintStmt)stmt.f0.choice;
			sexp_ = new MSimpleExp(print_stmt.f1);
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm!!!");
		}
	}
}
