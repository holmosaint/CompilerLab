package spiglet.symbol;

import java.util.*;

import spiglet.syntaxtree.*;

public class MProcedure {
	// a0-a3: ������Ӻ������ݵĲ���
	// t0-t9: �����ʱ���������ڷ�����������ʱ���ر������ǵ�����
	// s0-s7: ��žֲ��������ڷ�����������ʱһ��Ҫ�������ǵ�����
	// v0-v1: v0 ����Ӻ������ؽ����v0��v1�������ڱ���ʽ��ֵ����ջ�м���
	private static String registers[] = {"s0", "s1", "s2", "s3", "s4", "s5", 
										 "s6", "s7", "t0", "t1", "t2", "t3", 
										 "t4", "t5", "t6", "t7", "t8", "t9", 
										 "a0", "a1", "a2", "a3", "v0", "v1"};
	// ���label��ȫ�ֵģ����Ǿֲ���
	private String label_ = null;
	private int param_num_;
	private HashMap<String, MStmt> label2stmt_ = null;
	private ArrayList<MStmt> stmt_list_ = null;
	private MSimpleExp return_exp_ = null;
	private HashMap<MStmt, Integer> stmt2index_ = null;
	private ArrayList<ArrayList<Integer>> successors_ = null;
	// len(INs) == len(OUTs)
	// len(INs) = len(stmt_list_) + 1
	private ArrayList<HashSet<Integer>> INs_ = null;
	private ArrayList<HashSet<Integer>> OUTs_ = null;
	private HashMap<Integer, Integer> tmp2reg_ = null;
	private HashMap<Integer, Integer> reg2tmp_ = null;
	
	private void formStmtList(NodeListOptional stmts) {
		MStmt cur_stmt;
		stmt_list_ = new ArrayList<MStmt>();
		stmt2index_ = new HashMap<MStmt, Integer>();
		for (Node node : stmts.nodes) {
			cur_stmt = new MStmt((NodeSequence)node, this);
			stmt2index_.put(cur_stmt, stmt_list_.size());
			stmt_list_.add(cur_stmt);
			if (cur_stmt.getLabel() != null) {
				label2stmt_.put(cur_stmt.getLabel(), cur_stmt);
			}
		}
	}
	
	private void constructChain() {
		int num_stmt = stmt_list_.size();
		successors_ = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < num_stmt; i++) successors_.add(new ArrayList<Integer>());
		for (int i = 0; i < num_stmt - 1; i++) {
			if (!stmt_list_.get(i).isJump())
				successors_.get(i).add(i + 1);
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
