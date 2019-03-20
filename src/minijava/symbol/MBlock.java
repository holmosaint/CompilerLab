package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;

public class MBlock extends MScope {
	// attributes
	private MScope father_ = null;
	private ArrayList<MBlock> children_ = null;
	// 0->block, 1->assignment-statement, 2->arrayassignment-statement, 3->if-statement
	// 4->while-statement, 5->print-statement
	private int which_ = 0;
	private MExpr expression_ = null, index_expression_ = null;  // Expression in If or While statement
	private MVar var_ = null;
	
	// Constructor
	public MBlock(MScope father, NodeChoice node_choice) {
		father_ = father;
		which_ = node_choice.which;
		switch (which_) {
		case 0:
			// Block
			System.out.println(">>Block");
			parseStatement(((Block) node_choice.choice).f1, this);
			break;
		case 1:
			// AssignmentSatement
			System.out.println(">>Assignment");
			var_ = getVar(((AssignmentStatement) node_choice.choice).f0.f0.toString());
			expression_ = new MExpr(((AssignmentStatement) node_choice.choice).f2);
			break;
		case 2:
			// ArrayAssignment
			System.out.println(">>ArrayAssignment");
			var_ = getVar(((ArrayAssignmentStatement) node_choice.choice).f0.f0.toString());
			index_expression_ = new MExpr(((ArrayAssignmentStatement) node_choice.choice).f2);
			expression_ = new MExpr(((ArrayAssignmentStatement) node_choice.choice).f5);
			break;
		case 3:
			// IfStatement
			System.out.println(">>IfStatement");
			expression_ = new MExpr(((IfStatement) node_choice.choice).f2);
			parseStatement(((IfStatement) node_choice.choice).f4, this);
			parseStatement(((IfStatement) node_choice.choice).f6, this);
			break;
		case 4:
			// WhileStatement
			System.out.println(">>WhileStatement");
			expression_ = new MExpr(((WhileStatement) node_choice.choice).f2);
			parseStatement(((WhileStatement) node_choice.choice).f4, this);
			break;
		case 5:
			// PrintStatement
			System.out.println(">>PrintStatement");
			expression_ = new MExpr(((PrintStatement) node_choice.choice).f2);
			break;
		default:
			System.out.println("Uknown statement");
			System.exit(1);
		}
	}
	
	public void register() {
		// emm??
	}
	
	public void addBlock(MScope block) {
		if (children_ == null) {
			System.out.println("Create children_ in MBlock " + which_);
			children_ = new ArrayList<MBlock>();
		}
		children_.add((MBlock)block);
	}
	
	private MVar getVar(String name) {
		return null;
	}
}
