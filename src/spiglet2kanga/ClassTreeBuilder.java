package spiglet2kanga;

import spiglet.syntaxtree.*;
import spiglet.visitor.*;
import spiglet.symbol.*;

public class ClassTreeBuilder extends DepthFirstVisitor {
	public void visit(Goal goal) {
		SymbolTable.parse(goal);
	}
}
