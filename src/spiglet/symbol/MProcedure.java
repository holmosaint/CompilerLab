package spiglet.symbol;

import java.util.*;

import spiglet.syntaxtree.*;
import util.ErrorHandler;


public class MProcedure {
	// a0-a3: 存放向子函数传递的参数
	// t0-t9: 存放临时运算结果，在发生函数调用时不必保存它们的内容
	// s0-s7: 存放局部变量，在发生函数调用时一般要保存它们的内容
	// v0-v1: v0 存放子函数返回结果；v0、v1还可用于表达式求值，从栈中加载
	private static String registers[] = {"s0", "s1", "s2", "s3", "s4", "s5", 
										 "s6", "s7", "t0", "t1", "t2", "t3", 
										 "t4", "t5", "t6", "t7", "t8", "t9", 
										 "a0", "a1", "a2", "a3", "v0", "v1"};
	// 标号label是全局的，而非局部的
	private String label_ = null;
	private int param_num_;
	private HashMap<String, MStmt> label2stmt_ = null;
	private ArrayList<MStmt> stmt_list_ = null;
	private MSimpleExp return_exp_ = null;
	private HashMap<MStmt, Integer> stmt2index_ = null;
	private ArrayList<ArrayList<Integer>> successors_ = null;
	private ArrayList<ArrayList<Integer>> predecessors_ = null;
	// len(INs) == len(OUTs)
	// len(INs) = len(stmt_list_) + 1
	private ArrayList<HashSet<Integer>> INs_ = null;
	private ArrayList<HashSet<Integer>> OUTs_ = null;
	private HashMap<Integer, Integer> tmp2reg_ = null;
	private HashMap<Integer, Integer> reg2tmp_ = null;
	private HashMap<Integer, HashSet<Integer>> edges_ = null;
	
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
	
	// Construct the flow map
	private void constructMap() {
		// 1.Get the information about the subsequent relationship
		int num_stmt = stmt_list_.size();
		successors_ = new ArrayList<ArrayList<Integer>>();
		predecessors_ = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < num_stmt; i++) {
			successors_.add(new ArrayList<Integer>());
			predecessors_.add(new ArrayList<Integer>());
		}
		for (int i = 0; i < num_stmt - 1; i++) {
			if (!stmt_list_.get(i).isJump()) {
				successors_.get(i).add(i + 1);
				predecessors_.get(i + 1).add(i);
			}
		}
		for (int i = 0; i < num_stmt; i++) {
			String label = stmt_list_.get(i).getExtraSuccessor();
			if (label != null) {
				successors_.get(i).add(stmt2index_.get(label2stmt_.get(label)));
				predecessors_.get(stmt2index_.get(label2stmt_.get(label))).add(i);
			}
		}
		// 2.Live variables analysis
		HashSet<Integer> changed = new HashSet<Integer>();
		INs_ = new ArrayList<HashSet<Integer>>();
		OUTs_ = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < num_stmt; i++) {
			INs_.add(new HashSet<Integer>());
			OUTs_.add(new HashSet<Integer>());
			changed.add(i);
		}
		INs_.add(new HashSet<Integer>());
		OUTs_.add(new HashSet<Integer>());
		if (return_exp_.getUsedId() != -1) {
			INs_.get(num_stmt).add(return_exp_.getUsedId());
		}
		while (!changed.isEmpty()) {
			int cur = 0;
			for (; cur < num_stmt; cur++) {
				if (changed.contains(cur))
					break;
			}
			changed.remove(cur);
			
			OUTs_.get(cur).clear();
			for (Integer successor : successors_.get(cur)) {
				OUTs_.get(cur).addAll(INs_.get(successor));
			}
			if (cur == num_stmt - 1) {
				OUTs_.get(cur).addAll(INs_.get(num_stmt));
			}
			
			HashSet<Integer> mirror = (HashSet<Integer>)INs_.get(cur).clone();
			INs_.get(cur).clear();
			INs_.get(cur).addAll(OUTs_.get(cur));
			INs_.get(cur).removeAll(stmt_list_.get(cur).getDefinedIds());
			INs_.get(cur).addAll(stmt_list_.get(cur).getUsedIds());
			if (!INs_.get(cur).equals(mirror)) {
				for (Integer predecessor : predecessors_.get(cur)) {
					changed.add(predecessor);
				}
			}
		}
		// 3.Construct the conflict graph
		edges_ = new HashMap<Integer, HashSet<Integer>>();
		for (HashSet<Integer> id_set : INs_) {
			for (Integer id : id_set) {
				if (!edges_.containsKey(id)) {
					edges_.put(id, new HashSet<Integer>());
				}
				for (Integer conf_id : id_set) {
					if (conf_id == id) continue;
					else {
						edges_.get(id).add(conf_id);
					}
				}
			}
		}
		for (HashSet<Integer> id_set : OUTs_) {
			for (Integer id : id_set) {
				if (!edges_.containsKey(id)) {
					edges_.put(id, new HashSet<Integer>());
				}
				for (Integer conf_id : id_set) {
					if (conf_id == id) continue;
					else {
						edges_.get(id).add(conf_id);
					}
				}
			}
		}
		// 4.Graph coloring
		int spill_cnt = 0, cur_id = 0;
		final int reg_num = 15;
		boolean flag = false;
		Stack<Integer> stack = new Stack<Integer>();
		tmp2reg_ = new HashMap<Integer, Integer>();
		reg2tmp_ = new HashMap<Integer, Integer>();
		HashMap<Integer, HashSet<Integer>> edges = 
				(HashMap<Integer, HashSet<Integer>>) edges_.clone();
		while (!edges.isEmpty()) {
			flag = false;
			for (Integer id : edges.keySet()) {
				if (edges.get(id).size() < reg_num) {
					cur_id = id;
					flag = true;
					break;
				}
			}
			if (flag) {
				stack.push(cur_id);
			} else {
				tmp2reg_.put(cur_id, --spill_cnt);
				// reg2tmp_.put(spill_cnt, cur_id);
			}
			for (Integer conf_id : edges.get(cur_id)) {
				edges.get(conf_id).remove(cur_id);
			}
			edges.remove(cur_id);
		}
		if (spill_cnt != 0) {
			ErrorHandler.errorPrint("NMDWSM");
		}
		while (!stack.empty()) {
			cur_id = stack.peek();
			stack.pop();
			for (int i = 0; i < reg_num; i++) {
				flag = true;
				for (Integer conf_id : edges_.get(cur_id)) {
					if (tmp2reg_.containsKey(conf_id) &&
						tmp2reg_.get(conf_id) == i) {
						flag = false;
						break;
					}
				}
				if (flag) {
					tmp2reg_.put(cur_id, i);
					// reg2tmp_.put(i, cur_id);
					break;
				}
			}
		}
		// 5.Check
//		for (MStmt stmt : stmt_list_) {
//			for (Integer id : stmt.getDefinedIds()) {
//				if (!tmp2reg_.containsKey(id)) {
//					ErrorHandler.errorPrint("NMDWSM defined " + id);
//				}
//			}
//			for (Integer id : stmt.getUsedIds()) {
//				if (!tmp2reg_.containsKey(id)) {
//					ErrorHandler.errorPrint("NMDWSM used " + id);
//				}
//			}
//		}
	}
	
	// For Main procedure
	public MProcedure(Goal goal) {
		label_ = "MAIN";
		param_num_ = 0;
		label2stmt_ = new HashMap<String, MStmt>();
		formStmtList(goal.f1.f0);
		return_exp_ = new MSimpleExp();
		constructMap();
	}
	// For common procedure
	public MProcedure(Procedure procedure) {
		label_ = procedure.f0.f0.toString();
		param_num_ = Integer.parseInt(procedure.f2.f0.toString());
		label2stmt_ = new HashMap<String, MStmt>();
		formStmtList(procedure.f4.f1.f0);
		return_exp_ = new MSimpleExp(procedure.f4.f3);
		constructMap();
	}

	// For debugging
	public String getInfo() {
		String res = "";
		if (param_num_ == 0) {
			res += label_ + "\n";
		}
		else {
			res += label_ + " [" + param_num_ + "]\nBEGIN\n";
			int id = param_num_;
			for (; id < 1000; id++) {
				boolean flag = true;
				for (int i = 0; i < param_num_; i++) {
					if (tmp2reg_.containsKey(i) && id == tmp2reg_.get(i)) {
						flag = false;
						break;
					}
				}
				if (flag) break;
			}
			res += "\tMOVE TEMP " + id + " HALLOCATE " + (4 * param_num_) + "\n";
			for (int i = 0; i < param_num_; i++) {
				res += "\tHSTORE TEMP " + id + " " + (4 * i) + " TEMP " + i + "\n";
			}
			for (int i = 0; i < param_num_; i++) {
				if (tmp2reg_.containsKey(i))
					res += "\tHLOAD TEMP " + tmp2reg_.get(i) + " TEMP " + id + " " + (4 * i) + "\n";
			}
		}
		for (int i = 0; i < stmt_list_.size(); i++) {
			res += stmt_list_.get(i).getInfo(tmp2reg_, OUTs_.get(i));
			
//			res += "\tsuccessors:";
//			for (Integer integer : successors_.get(i)) {
//				res += integer + " ";
//			}
//			res += "\n";
//			res += "\tpredecessors:";
//			for (Integer integer : predecessors_.get(i)) {
//				res += integer + " ";
//			}
//			res += "\n";
//			res += "\tINs:";
//			for (Integer integer : INs_.get(i)) {
//				res += integer + " ";
//			}
//			res += "\n";
//			res += "\tOUTs:";
//			for (Integer integer : OUTs_.get(i)) {
//				res += integer + " ";
//			}
//			res += "\n";
//			res += "\ttmp2reg:{ ";
//			for (Integer tmp_id : tmp2reg_.keySet()) {
//				res += tmp_id + ": " + tmp2reg_.get(tmp_id) + ", ";
//			}
//			res += "}\n";
			
		}
		if (param_num_ != 0) {
			res += "RETURN\n";
			res += "\t" + return_exp_.getInfo(tmp2reg_) + "\n";
		}
		res += "END\n";
		return res;
	}
}
