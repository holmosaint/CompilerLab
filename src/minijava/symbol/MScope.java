package minijava.symbol;

import minijava.syntaxtree.*;

public abstract class MScope {
	public abstract void addBlock(MScope scope);
	public abstract MScope getFather();
	public abstract MVar queryVar(String var_name);
	public static MBlock getBlock(NodeChoice choice, MScope father) {
		MBlock block = null;

		return block;
	}
	
	public static void parseStatement(NodeListOptional statement_list, MScope father) {
		for (Node node : statement_list.nodes) {
			parseStatement((Statement) node, father);
		}
	}
	
	public static void parseStatement(Statement statement, MScope father) {
		father.addBlock(new MBlock(father, statement.f0));
	}
}
