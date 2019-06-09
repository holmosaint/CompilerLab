package kanga2mips;
import kanga.syntaxtree.*;
import kanga.visitor.*;
import kanga.symbol.*;

public class ClassTreeBuilder extends DepthFirstVisitor {
	public void visit(Goal goal) {
		SymbolTable.parse(goal);
	}
}
