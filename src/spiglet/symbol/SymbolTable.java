package spiglet.symbol;

import java.io.*;
import java.util.*;

import spiglet.SpigletParser;
import spiglet.syntaxtree.*;
import util.ErrorHandler;
import spiglet.*;

public class SymbolTable {
	private static HashMap<String, MProcedure> label2procedure_;
	public static MProcedure main_procedure_;
	public static void parse(Goal goal) {
		// Get Main procedure
		label2procedure_ = new HashMap<String, MProcedure>();
		main_procedure_ = new MProcedure(goal);
		label2procedure_.put("MAIN", main_procedure_);
		// Get other procedures
		for (Node node : goal.f3.nodes) {
			Procedure procedure = (Procedure)node;
			label2procedure_.put(procedure.f0.f0.toString(),
								 new MProcedure(procedure));
		}
	}
	
	public static MProcedure getProcedure(String label) {
		return label2procedure_.get(label);
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
