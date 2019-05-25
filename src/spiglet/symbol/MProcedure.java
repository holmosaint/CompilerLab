package spiglet.symbol;

import java.util.*;

import spiglet.syntaxtree.*;

public class MProcedure {
	// 标号label是全局的，而非局部的
	private String label_ = null;
	private int param_num_;
	private HashMap<String, MStmt> label2stmt_ = null;
	private ArrayList<MStmt> stmt_list_ = null;
	private MSimpleExp return_exp_ = null;

	private void formStmtList(NodeListOptional stmts) {
		MStmt cur_stmt;
		stmt_list_ = new ArrayList<MStmt>();
		for (Node node : stmts.nodes) {
			cur_stmt = new MStmt((NodeSequence)node, this);
			stmt_list_.add(cur_stmt);
			if (cur_stmt.getLabel() != null) {
				label2stmt_.put(cur_stmt.getLabel(), cur_stmt);
			}
		}
	}
	
	private void constructChain() {
		int num_stmt = stmt_list_.size();
		for (int i = 1; i < num_stmt; i++) {
			if (!stmt_list_.get(i - 1).isJump())
				stmt_list_.get(i).addFormer(stmt_list_.get(i - 1));
		}
		for (MStmt stmt : stmt_list_) {
			stmt.constructChain();
		}
	}
	
	// For Main procedure
	public MProcedure(Goal goal) {
		label_ = "MAIN";
		param_num_ = 0;
		label2stmt_ = new HashMap<String, MStmt>();
		formStmtList(goal.f1.f0);
		constructChain();
	}
	// For common procedure
	public MProcedure(Procedure procedure) {
		label_ = procedure.f0.f0.toString();
		param_num_ = Integer.parseInt(procedure.f2.f0.toString());
		label2stmt_ = new HashMap<String, MStmt>();
		formStmtList(procedure.f4.f1.f0);
		return_exp_ = new MSimpleExp(procedure.f4.f3);
		constructChain();
	}
	
	public MStmt getStmtByLabel(String label) {
		return label2stmt_.get(label);
	}

	// For debugging
	public String getInfo() {
		String res = "";
		res += "Procedure " + label_ + "\n";
		for (MStmt stmt : stmt_list_) {
			res += stmt.getInfo();
		}
		return res;
	}
}
