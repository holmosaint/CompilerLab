package kanga.symbol;

import java.util.*;

import kanga.syntaxtree.*;

public class MProcedure {
	public static String registers_[] = {"$a0", "$a1", "$a2", "$a3", "$t0", "$t1", 
										 "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", 
										 "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", 
										 "$s6", "$s7", "$t8", "$t9", "$v0", "$v1"};
	private String label_ = null;
	private int param_num_, stack_cell_num_, max_param_num_;
	private ArrayList<MStmt> stmt_list_ = null;
	private HashMap<String, MStmt> label2stmt_ = null;
	private HashMap<MStmt, Integer> stmt2index_ = null;
	
	private void formStmtList(NodeListOptional stmts) {
		MStmt cur_stmt;
		stmt_list_ = new ArrayList<MStmt>();
		stmt2index_ = new HashMap<MStmt, Integer>();
		label2stmt_ = new HashMap<String, MStmt>();
		for (Node node : stmts.nodes) {
			cur_stmt = new MStmt((NodeSequence)node, this);
			stmt2index_.put(cur_stmt, stmt_list_.size());
			stmt_list_.add(cur_stmt);
			if (cur_stmt.getLabel() != null) {
				label2stmt_.put(cur_stmt.getLabel(), cur_stmt);
			}
		}
	}
	
	public MProcedure(Goal goal) {
		label_ = "main";
		param_num_ = Integer.parseInt(goal.f2.f0.toString());
		stack_cell_num_ = Integer.parseInt(goal.f5.f0.toString());
		max_param_num_ = Integer.parseInt(goal.f8.f0.toString());
		formStmtList(goal.f10.f0);
	}

	public MProcedure(Procedure procedure) {
		label_ = procedure.f0.f0.toString();
		param_num_ = Integer.parseInt(procedure.f2.f0.toString());
		stack_cell_num_ = Integer.parseInt(procedure.f5.f0.toString());
		max_param_num_ = Integer.parseInt(procedure.f8.f0.toString());
		formStmtList(procedure.f10.f0);
	}

	public int getSpillParamCnt() {
		return Math.max(0, max_param_num_ - 4);
	}
	
	public int getUpStackParamCnt() {
		return Math.max(0, param_num_ - 4);
	}
	
	public String toMIPS(boolean is_main) {
		String res = "\t\t.text\n";
		int offset = 0, stack_size;
		res += "\t\t.globl\t\t" + label_ + "\n";
		res += label_ + ":\n";
		if (!is_main) {
			res += "\t\tsw $fp, -8($sp)\n";
			offset = 4;
		}
		res += "\t\tmove $fp, $sp\n";
		stack_size = offset + 4 + 4 * stack_cell_num_ + 4 * Math.max(0, max_param_num_ - 4);
		res += "\t\tsubu $sp, $sp, " + stack_size + "\n";
		res += "\t\tsw $ra, -4($fp)\n";
		for (MStmt stmt: stmt_list_) {
			res += stmt.toMIPS();
		}
		res += "\t\tlw $ra, -4($fp)\n";
		if (!is_main) {
			res += "\t\tlw $fp, " + (stack_size - 8) + "($sp)\n";
		}
		res += "\t\taddu $sp, $sp, " + stack_size + "\n";
		res += "\t\tj $ra\n\n";
		return res;
	}
	
	// For debugging
	public String getInfo() {
		String res = "";
		res += label_ + "[" + param_num_ + "] [" + stack_cell_num_ +
			   "] [" + max_param_num_ + "]\n";
		for (MStmt stmt : stmt_list_) {
			res += stmt.getInfo();
		}
		res += "END\n";
		return res;
	}
}
