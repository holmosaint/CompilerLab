package minijava.symbol;

import minijava.syntaxtree.*;

public abstract class MScope {
	public abstract void addBlock(MScope scope);
	
	public static void parseStatement(NodeListOptional statement_list, MScope father) {
		for (Node node : statement_list.nodes) {
			parseStatement((Statement) node, father);
		}
	}
	
	public static void parseStatement(Statement statement, MScope father) {
		father.addBlock(new MBlock(father, statement.f0));
	}
}
