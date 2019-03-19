package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;

public class MBlock {
	// attributes
	private MBlock block_father_ = null;
	private MMethod method_father_ = null;
	private ArrayList<MBlock> children_ = new ArrayList<MBlock>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	// Constructor
	public MBlock(MBlock block_father, Node node) {
		this(node);
		block_father_ = block_father;
	}
	
	public MBlock(MMethod method_father, Node node) {
		this(node);
		method_father_ = method_father;
	}
	
	private MBlock(Node node) {
		if (node instanceof Block) {
			for (Node n : ((Block) node).f1.nodes) {
				 Statement statement = (Statement) n;
				 System.out.println(statement.f0.which);
			}
		} else {
			System.out.println("Encounter wrong parameter when constructing MBlock");
			System.exit(1);
		}
		
	}
	
	public void Register() {
		// emm??
	}
	
	public void AddBlock(MBlock block) {
		children_.add(block);
	}
	
	public void AddVar(MVar var) {
		if (CheckVar(var)) {
			System.out.println("Duplicate declaration of variable " + var.getName() + " in block");
			System.exit(1);
		} else {
			vars_.put(var.getName(), var);
		}
	}
	
	private boolean CheckVar(MVar var) {
		return vars_.containsKey(var.getName());
	}
}
