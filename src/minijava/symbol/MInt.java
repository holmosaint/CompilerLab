package minijava.symbol;

import minijava.syntaxtree.Node;

public class MInt extends MType {

	
	public String getName() {
		return "int";
	}

	public int getSize() {
		return 4;
	}

	public boolean isAssignable(MType target, Node n) {
		if (target instanceof MInt)
			return true;
		else
			return false;
	}

}
