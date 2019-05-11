package minijava.symbol;

import java.util.ArrayList;

import minijava.syntaxtree.*;
import minijava2piglet.minijava2piglet;
import minijava2spiglet.minijava2spiglet;
import util.ErrorHandler;

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
			// System.out.println(">>Block");
			parseStatement(((Block) node_choice.choice).f1, this);
			break;
		case 1:
			// AssignmentSatement
			// System.out.println(">>Assignment");
			var_name_ = ((AssignmentStatement) node_choice.choice).f0.f0.toString();
			expression_ = new MExpr(((AssignmentStatement) node_choice.choice).f2, this);
			break;
		case 2:
			// ArrayAssignment
			// System.out.println(">>ArrayAssignment");
			var_name_ = ((ArrayAssignmentStatement) node_choice.choice).f0.f0.toString();
			index_expression_ = new MExpr(((ArrayAssignmentStatement) node_choice.choice).f2, this);
			expression_ = new MExpr(((ArrayAssignmentStatement) node_choice.choice).f5, this);
			break;
		case 3:
			// IfStatement
			// System.out.println(">>IfStatement");
			expression_ = new MExpr(((IfStatement) node_choice.choice).f2, this);
			parseStatement(((IfStatement) node_choice.choice).f4, this);
			divide = children_.size();  // The variable "divide" is prepared for the further task
			parseStatement(((IfStatement) node_choice.choice).f6, this);
			break;
		case 4:
			// WhileStatement
			// System.out.println(">>WhileStatement");
			expression_ = new MExpr(((WhileStatement) node_choice.choice).f2, this);
			parseStatement(((WhileStatement) node_choice.choice).f4, this);
			break;
		case 5:
			// PrintStatement
			// System.out.println(">>PrintStatement");
			expression_ = new MExpr(((PrintStatement) node_choice.choice).f2, this);
			break;
		default:
			System.out.println("Unknown statement");
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
		String errorMsg = "";
		switch (which_) {
		case 0:
			// Block
			for (MBlock child : children_) {
				child.register();
			}
			break;
		case 1:
			// AssignmentStatement
			// AssignmentStatement	::=	Identifier "=" Expression ";"
			var_ = queryVar(var_name_);
			if(var_ == null) {
				errorMsg = "The var " + var_name_ + " is not defined!\n"; 
				ErrorHandler.errorPrint(errorMsg);
			}
			expression_.register();
			if (!var_.getType().isAssignable(expression_.getType())) {
				errorMsg = "Type mismatch in AssignmentStatement: " + var_.getType().getName() 
						   + " " + var_name_ + " versus " + expression_.getType().getName() 
						   + " " + expression_.getWhich();
				ErrorHandler.errorPrint(errorMsg);
			}
			var_.assign();
			var_.allocate(expression_);
			break;
		case 2:
			// ArrayAssignment
			var_ = queryVar(var_name_);
			if(var_ == null) {
				errorMsg = "The var " + var_name_ + " is not defined!\n"; 
				ErrorHandler.errorPrint(errorMsg);
			}
			if (!(var_.getType() instanceof MArray)) {
				errorMsg = "ArrayAssignment's var should be an array, but get "
						   + var_.getType().getName(); 
				ErrorHandler.errorPrint(errorMsg);
			}
			
			index_expression_.register();
			if (!(index_expression_.getType() instanceof MInt)) {
				errorMsg = "Array index expression should be an integer, but get "
						   + index_expression_.getType().getName(); 
				ErrorHandler.errorPrint(errorMsg);
			}
			
			expression_.register();
			if (!(expression_.getType() instanceof MInt)) {
				errorMsg = "ArrayAssignment's expression should be an integer, but get "
						   + expression_.getType().getName(); 
				ErrorHandler.errorPrint(errorMsg);
			}
			
			// Check the index
			int length = var_.getLength();
			int index = index_expression_.getInteger();
			
			if (length != -1 && index != -1 && length <= index) {
				errorMsg = "Array index is wrong " + index + "/" + length;
				ErrorHandler.errorPrint(errorMsg);
			}
			break;
		case 3:
			// IfStatement
			expression_.register();
			if (!(expression_.getType() instanceof MBool)) {
				errorMsg = "Expression in IfStatement should be a bool, but get " 
						   + expression_.getType().getName(); 
				ErrorHandler.errorPrint(errorMsg);
			}
			for (MBlock child : children_) {
				child.register();
			}
			break;
		case 4:
			// WhileStatement
			expression_.register();
			if (!(expression_.getType() instanceof MBool)) {
				errorMsg = "Expression in WhileStatement should be a bool, but get " 
						   + expression_.getType().getName(); 
				ErrorHandler.errorPrint(errorMsg);
			}
			for (MBlock child : children_) {
				child.register();
			}
			break;
		case 5:
			// PrintStatement
			expression_.register();
			if (!(expression_.getType() instanceof MInt)) {
				errorMsg = "Expression in PrintStatement should be an integer, but get " 
						   + expression_.getType().getName(); 
				ErrorHandler.errorPrint(errorMsg);
			}
			break;
		default:
			break;
		}
	}
	
	public void addBlock(MScope block) {
		if (children_ == null) {
			// System.out.println("Create children_ in MBlock " + which_);
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
	
	public int getWhich() {
		return which_;
	}
	
	// 判断在哪个method里面
	public MMethod getMethodScope() {
		MScope father = father_;
		while(!(father instanceof MMethod)) {
			father = father.getFather();
			assert father!=null: "The father of a block is null!\n";
		}
		return (MMethod)father;
	}
	
	// below for piglet code generation
	public String generatePigletBlockCode(int tab, boolean write) {
		String code = "";
		String prefixTab = "";
		for(int i = 0;i < tab; ++i)
			prefixTab += "\t";
		
		MMethod method = getMethodScope();
		String localTemp = "";
		String exprTemp1 = "", exprTemp2 = "";
		boolean isLocal = false;
		int label1 = -1, label2 = -1, label3 = -1;
		if(var_ != null) 
			isLocal = method.judgeLocalVar(var_);
		
		switch (which_) {
		case 0:
			// block
			for(MBlock block : children_)
				block.generatePigletBlockCode(tab, write);
			break;
		
		case 1:
			// Assignment expression
			// AssignmentStatement	::=	Identifier "=" Expression ";"
			exprTemp1 = expression_.generatePigletExpressionCode(tab, write);
			code += prefixTab;
			if(isLocal) {
				code += "MOVE TEMP " + var_.getPigletTempID() + " ";
			}
			else {
				int offset = -1;
				MClass owner = getMethodScope().getOwner();
				offset = owner.queryVarOffset(var_.getName());
				code += "HSTORE TEMP 0 " + offset  + " ";
			}
			// get the register that contains the return value
			code += exprTemp1 + "\n";
			minijava2piglet.writeCode(code);
			break;
			
		case 2:
			// Array assignment expression
			// ArrayAssignmentStatement	::=	Identifier "[" Expression "]" "=" Expression ";"
			localTemp = minijava2piglet.TEMP + minijava2piglet.getTempIndex();
			String localTemp2 = minijava2piglet.TEMP + minijava2piglet.getTempIndex();
			label1 = minijava2piglet.getLabelIndex();
			label2 = minijava2piglet.getLabelIndex();
			exprTemp1 = index_expression_.generatePigletExpressionCode(tab, write);
			exprTemp2 = expression_.generatePigletExpressionCode(tab, write);
			if(isLocal) {
				code += prefixTab + "MOVE " + localTemp + " TEMP " + var_.getPigletTempID() + "\n";
			}
			else {
				int offset = -1;
				MClass owner = getMethodScope().getOwner();
				offset = owner.queryVarOffset(var_.getName());
				code += prefixTab + "HLOAD " + localTemp + " TEMP 0 " + offset + "\n";
			}
			code += prefixTab + "HLOAD " + localTemp2 + " " + localTemp + " 0\n";
			code += prefixTab + "CJUMP LT " + localTemp2 + " PLUS 1 " + exprTemp1 + " L" + label2 + "\n";
			code += prefixTab + "L" + label1 + " ERROR\n";
			code += prefixTab + "L" + label2 + "\n";
			code += prefixTab + "\tHSTORE PLUS " + localTemp + " TIMES PLUS " + exprTemp1 + " 1 4 0 " + exprTemp2 + "\n";
			minijava2piglet.writeCode(code);
			break;
			
		case 3:
			// If assignment
			// IfStatement	::=	"if" "(" Expression ")" Statement "else" Statement
			label1 = minijava2piglet.getLabelIndex();
			label2 = minijava2piglet.getLabelIndex();
			label3 = minijava2piglet.getLabelIndex();
			exprTemp1 = expression_.generatePigletExpressionCode(tab, write);
			code += prefixTab + "CJUMP " + exprTemp1 + " L" + label2 + "\n";
			code += prefixTab + "L" + label1 + "\n";
			minijava2piglet.writeCode(code);
			code = "";
			children_.get(0).generatePigletBlockCode(tab + 1, write);
			code += prefixTab + "\t" + "JUMP L" + label3 + "\n";
			code += prefixTab + "L" + label2 + "\n";
			minijava2piglet.writeCode(code);
			code = "";
			children_.get(1).generatePigletBlockCode(tab + 1, write);
			code += prefixTab + "L" + label3 + "\tNOOP\n";
			minijava2piglet.writeCode(code);
			break;
			
		case 4:
			// While assignment
			// WhileStatement	::=	"while" "(" Expression ")" Statement
			label1 = minijava2piglet.getLabelIndex();
			label2 = minijava2piglet.getLabelIndex();
			code += prefixTab + "L" + label1  + "\n";
			minijava2piglet.writeCode(code);
			code = "";
			exprTemp1 = expression_.generatePigletExpressionCode(tab + 1, write);
			code += prefixTab + "\tCJUMP " + exprTemp1 + " L" + label2 + "\n";
			minijava2piglet.writeCode(code);
			code = "";
			children_.get(0).generatePigletBlockCode(tab + 1, write);
			code += prefixTab + "\tJUMP L" + label1 + "\n";
			code += prefixTab + "L" + label2 + "\tNOOP\n";
			minijava2piglet.writeCode(code);
			break;
			
		case 5:
			// Print assignment
			// PrintStatement	::=	"System.out.println" "(" Expression ")" ";"
			exprTemp1 = expression_.generatePigletExpressionCode(tab, write);
			code += prefixTab + "PRINT " + exprTemp1 + "\n";
			minijava2piglet.writeCode(code);
			break;
			
		default:
			break;
		}
		return code;
	}

	public String generateSpigletBlockCode(int tab, boolean write) {
		String code = "";
		String prefixTab = "";
		for(int i = 0;i < tab; ++i)
			prefixTab += "\t";
		
		MMethod method = getMethodScope();
		String localTemp = "";
		String exprTemp1 = "", exprTemp2 = "";
		boolean isLocal = false;
		int label1 = -1, label2 = -1, label3 = -1;
		if(var_ != null) 
			isLocal = method.judgeLocalVar(var_);
		
		switch (which_) {
		case 0:
			// block
			for(MBlock block : children_)
				block.generateSpigletBlockCode(tab, write);
			break;
		
		case 1:
			// Assignment expression
			// AssignmentStatement	::=	Identifier "=" Expression ";"
			exprTemp1 = expression_.generateSpigletExpressionCode(tab, write);
			code += prefixTab;
			if(isLocal) {
				code += "MOVE TEMP " + var_.getSpigletTempID() + " ";
			}
			else {
				int offset = -1;
				MClass owner = getMethodScope().getOwner();
				offset = owner.queryVarOffset(var_.getName());
				code += "HSTORE TEMP 0 " + offset  + " ";
			}
			// get the register that contains the return value
			code += exprTemp1 + "\n";
			minijava2spiglet.writeCode(code);
			break;
			
		case 2:
			// Array assignment expression
			// ArrayAssignmentStatement	::=	Identifier "[" Expression "]" "=" Expression ";"
			localTemp = minijava2spiglet.TEMP + minijava2spiglet.getTempIndex();
			String localTemp2 = minijava2spiglet.TEMP + minijava2spiglet.getTempIndex();
			String localTemp3 = minijava2spiglet.TEMP + minijava2spiglet.getTempIndex();
			label1 = minijava2spiglet.getLabelIndex();
			label2 = minijava2spiglet.getLabelIndex();
			exprTemp1 = index_expression_.generateSpigletExpressionCode(tab, write);
			exprTemp2 = expression_.generateSpigletExpressionCode(tab, write);
			if(isLocal) {
				code += prefixTab + "MOVE " + localTemp + " TEMP " + var_.getSpigletTempID() + "\n";
			}
			else {
				int offset = -1;
				MClass owner = getMethodScope().getOwner();
				offset = owner.queryVarOffset(var_.getName());
				code += prefixTab + "HLOAD " + localTemp + " TEMP 0 " + offset + "\n";
			}
			code += prefixTab + "HLOAD " + localTemp2 + " " + localTemp + " 0\n";
			code += prefixTab + "MOVE " + localTemp3 + " PLUS 1 " + exprTemp1 + "\n";
			code += prefixTab + "MOVE " + localTemp2 + " LT " + localTemp2 + " " + localTemp3 + "\n";
			code += prefixTab + "CJUMP " + localTemp2 + " L" + label2 + "\n"; 
			code += prefixTab + "L" + label1 + " ERROR\n";
			code += prefixTab + "L" + label2 + "\n";
			code += prefixTab + "\tMOVE " + localTemp2 + " PLUS " + exprTemp1 + " 1\n"; // offset
			code += prefixTab + "\tMOVE " + localTemp2 + "TIMES " + localTemp2 + " 4\n";
			code += prefixTab + "\tMOVE " + localTemp + " PLUS " + localTemp + " " + localTemp2 + "\n";
			code += prefixTab + "\tHSTORE " + localTemp + " 0 " + exprTemp2 + "\n";
			minijava2spiglet.writeCode(code);
			break;
			
		case 3:
			// If assignment
			// IfStatement	::=	"if" "(" Expression ")" Statement "else" Statement
			label1 = minijava2spiglet.getLabelIndex();
			label2 = minijava2spiglet.getLabelIndex();
			label3 = minijava2spiglet.getLabelIndex();
			exprTemp1 = expression_.generateSpigletExpressionCode(tab, write);
			code += prefixTab + "CJUMP " + exprTemp1 + " L" + label2 + "\n";
			code += prefixTab + "L" + label1 + "\n";
			minijava2spiglet.writeCode(code);
			code = "";
			children_.get(0).generateSpigletBlockCode(tab + 1, write);
			code += prefixTab + "\t" + "JUMP L" + label3 + "\n";
			code += prefixTab + "L" + label2 + "\n";
			minijava2spiglet.writeCode(code);
			code = "";
			children_.get(1).generateSpigletBlockCode(tab + 1, write);
			code += prefixTab + "L" + label3 + "\tNOOP\n";
			minijava2spiglet.writeCode(code);
			break;
			
		case 4:
			// While assignment
			// WhileStatement	::=	"while" "(" Expression ")" Statement
			label1 = minijava2spiglet.getLabelIndex();
			label2 = minijava2spiglet.getLabelIndex();
			code += prefixTab + "L" + label1  + "\n";
			minijava2spiglet.writeCode(code);
			code = "";
			exprTemp1 = expression_.generateSpigletExpressionCode(tab + 1, write);
			code += prefixTab + "\tCJUMP " + exprTemp1 + " L" + label2 + "\n";
			minijava2spiglet.writeCode(code);
			code = "";
			children_.get(0).generateSpigletBlockCode(tab + 1, write);
			code += prefixTab + "\tJUMP L" + label1 + "\n";
			code += prefixTab + "L" + label2 + "\tNOOP\n";
			minijava2spiglet.writeCode(code);
			break;
			
		case 5:
			// Print assignment
			// PrintStatement	::=	"System.out.println" "(" Expression ")" ";"
			exprTemp1 = expression_.generateSpigletExpressionCode(tab, write);
			code += prefixTab + "PRINT " + exprTemp1 + "\n";
			minijava2spiglet.writeCode(code);
			break;
			
		default:
			break;
		}
		return code;
	}
}
