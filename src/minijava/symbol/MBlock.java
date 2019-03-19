package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;

public class MBlock extends MScope {
	// attributes
	private MScope father_ = null;
	private ArrayList<MBlock> children_ = new ArrayList<MBlock>();
	private int choice_ = 0;  // 0->block, 1->If statement, 2->While statement
	private MExpr expression_ = null;  // Expression in If or While statement
	
	// Constructor
	public MBlock(MScope father, Node node) {
		this(node);
		father_ = father;
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
}
