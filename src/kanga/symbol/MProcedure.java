package kanga.symbol;

import java.util.*;

import kanga.syntaxtree.*;

public class MProcedure {
	private String label_ = null;
	private int param_num_, stack_cell_num_, max_param_num_;
	private ArrayList<MStmt> stmt_list_ = null;
	private HashMap<String, MStmt> label2stmt_ = null;
	private HashMap<MStmt, Integer> stmt2index_ = null;
	
	private void formStmtList(NodeListOptional stmts) {
		
	}
	
	public MProcedure(Goal goal) {
		
	}

	public MProcedure(Procedure procedure) {
		label_ = procedure.f0.f0.toString();
		
	}

}
