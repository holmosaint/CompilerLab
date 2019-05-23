package spiglet.symbol;

import java.util.*;

import spiglet.syntaxtree.*;

public class MProcedure {
	private String label_ = null;
	private int param_num_;
	private ArrayList<MStmt> stmt_list_ = null;
	private MSimpleExp return_exp_ = null;
	
	// For Main procedure
	public MProcedure(Goal goal) {
		label_ = "MAIN";
		param_num_ = 0;
		formStmtList(goal.f1.f0);
		return_exp_ = null;
	}
	// For common procedure
	public MProcedure(Procedure procedure) {
		label_ = procedure.f0.f0.toString();
		param_num_ = Integer.parseInt(procedure.f2.f0.toString());
		formStmtList(procedure.f4.f1.f0);
		return_exp_ = new MSimpleExp(procedure.f4.f3);
	}
	
	private void formStmtList(NodeListOptional stmts) {
		stmt_list_ = new ArrayList<MStmt>();
		for (Node node : stmts.nodes) {
			stmt_list_.add(new MStmt((NodeSequence)node));
		}
	}
}
