package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;

public class MBlock extends MScope {
	// attributes
	private MScope father_ = null;
	private int divide = 0;
	private ArrayList<MBlock> children_ = null;
	// 0->block
	// 1->assignment statement
	// 2->array assignment statement
	// 3->if statement
	// 4->while statement
	// 5->print statement
	private int which_ = 0;
	private MExpr expression_ = null, index_expression_ = null;  // Expression in If or While statement
	private String var_name_ = null;
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
			var_name_ = ((AssignmentStatement) node_choice.choice).f0.f0.toString();
			expression_ = new MExpr(((AssignmentStatement) node_choice.choice).f2, this);
			break;
		case 2:
			// ArrayAssignment
			System.out.println(">>ArrayAssignment");
			var_name_ = ((ArrayAssignmentStatement) node_choice.choice).f0.f0.toString();
			index_expression_ = new MExpr(((ArrayAssignmentStatement) node_choice.choice).f2, this);
			expression_ = new MExpr(((ArrayAssignmentStatement) node_choice.choice).f5, this);
			break;
		case 3:
			// IfStatement
			System.out.println(">>IfStatement");
			expression_ = new MExpr(((IfStatement) node_choice.choice).f2, this);
			parseStatement(((IfStatement) node_choice.choice).f4, this);
			divide = children_.size();  // The variable "divide" is prepared for the further task
			parseStatement(((IfStatement) node_choice.choice).f6, this);
			break;
		case 4:
			// WhileStatement
			System.out.println(">>WhileStatement");
			expression_ = new MExpr(((WhileStatement) node_choice.choice).f2, this);
			parseStatement(((WhileStatement) node_choice.choice).f4, this);
			break;
		case 5:
			// PrintStatement
			System.out.println(">>PrintStatement");
			expression_ = new MExpr(((PrintStatement) node_choice.choice).f2, this);
			break;
		default:
			System.out.println("Uknown statement");
			System.exit(1);
		}
	}
	
	public void register() {
		// check whether the variable in the block has been defined
		MScope father = father_;
		while(!(father instanceof MMethod)) {
			father = father.getFather();
			assert father!=null: "The father of a block is null!\n";
		}
		
		switch (which_) {
		case 0:
			// Block
			for (MBlock child : children_) {
				child.register();
			}
			break;
		case 1:
			// AssignmentSatement
			var_ = queryVar(var_name_);
			if(var_ == null) {
				System.out.printf("The var [%s] is not defined!\n", var_.getName());
				System.exit(1);
			}
			expression_.register();
			break;
		case 2:
			// ArrayAssignment
			var_ = queryVar(var_name_);
			if(var_ == null) {
				System.out.printf("The var [%s] is not defined!\n", var_.getName());
				System.exit(1);
			}
			index_expression_.register();
			expression_.register();
			break;
		case 3:
			// IfStatement
			expression_.register();
			for (MBlock child : children_) {
				child.register();
			}
			break;
		case 4:
			// WhileStatement
			expression_.register();
			for (MBlock child : children_) {
				child.register();
			}
			break;
		case 5:
			// PrintStatement
			expression_.register();
			break;
		default:
			break;
		}
	}

	/*
	void registerStatement() {
		if(expression_ == null)
			return;
		String errorMsg = "";
		switch (which_) {
			case 0:
				// Block
				return;
			case 1:
				// AssignmentSatement
				expression_.register();
				System.out.println(">>Assignment");
				break;
			case 2:
				// ArrayAssignment
				expression_.register();
				index_expression_.register();
				System.out.println(">>ArrayAssignment");
				break;
			case 3:
				// IfStatement
				expression_.register();
				if(expression_.getType() instanceof MBool)
					return;
				errorMsg = "The expression in the if statement is not a boolean type!";
				break;
			case 4:
				// WhileStatement
				expression_.register();
				if(expression_.getType() instanceof MBool)
					return;
				errorMsg = "The expression in the while statement is not a boolean type!";
				break;
			case 5:
				// PrintStatement
				expression_.register();
				if(expression_.getType() instanceof MInt)
					return;
				errorMsg = "The expression in the print statement is not a int type!";
				break;
			default: break;
		}
		System.out.println(errorMsg);
		System.exit(1);
	}
	*/
	
	public void addBlock(MScope block) {
		if (children_ == null) {
			System.out.println("Create children_ in MBlock " + which_);
			children_ = new ArrayList<MBlock>();
		}
		children_.add((MBlock)block);
	}
	
	public MScope getFather() {
		return father_;
	}

	public MVar queryVar(String var_name) {
		return father_.queryVar(var_name);
	}
}
