package kanga.symbol;

import java.util.HashMap;

import kanga.symbol.*;
import kanga.syntaxtree.*;

public class SymbolTable {

	private static HashMap<String, MProcedure> label2procedure_;
	public static MProcedure main_procedure_;
	public static void parse(Goal goal) {
		// Get Main procedure
		label2procedure_ = new HashMap<String, MProcedure>();
		main_procedure_ = new MProcedure(goal);
		label2procedure_.put("MAIN", main_procedure_);
		// Get other procedures
		for (Node node : goal.f12.nodes) {
			Procedure procedure = (Procedure)node;
			label2procedure_.put(procedure.f0.f0.toString(),
								 new MProcedure(procedure));
		}
	}
	
	public static MProcedure getProcedure(String label) {
		return label2procedure_.get(label);
	}
	
	public static String toMIPS() {
		String res = "";
		for (String label : label2procedure_.keySet()) {
			if (label.equals("MAIN")) {
				res += label2procedure_.get(label).toMIPS(true);
			}
		}
		for (String label : label2procedure_.keySet()) {
			if (!label.equals("MAIN")) {
				res += label2procedure_.get(label).toMIPS(false);
			}
		}
		res += "\t.text\n" + 
			   "\t.globl _halloc\n" + 
			   "_halloc:\n" + 
			   "\tli $v0, 9\n" + 
			   "\tsyscall\n" + 
				"\tj $ra\n" + 
				"\n" + 
				"\t.text\n" + 
				"\t.globl _print\n" + 
				"_print:\n" + 
				"\tli $v0, 1\n" + 
				"\tsyscall\n" + 
				"\tla $a0, newl\n" + 
				"\tli $v0, 4\n" + 
				"\tsyscall\n" + 
				"\tj $ra\n" + 
				"\n" + 
				"\t.data\n" + 
				"\t.align\t0\n" + 
				"newl:\t.asciiz \"\\n\" \n" + 
				"\t.data\n" + 
				"\t.align\t0\n" + 
				"str_er:\t.asciiz \" ERROR: abnormal termination\\n\" ";
		return res;
	}
	
	// For debugging
	public static void printInfo() {
		for (String label : label2procedure_.keySet()) {
			if (label.equals("MAIN")) {
				System.out.println(label2procedure_.get(label).getInfo());
			}
		}
		
		for (String label : label2procedure_.keySet()) {
			if (!label.equals("MAIN")) {
				System.out.println(label2procedure_.get(label).getInfo());
			}
		}
	}
}
