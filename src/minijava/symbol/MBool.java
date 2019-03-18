package minijava.symbol;

import minijava.syntaxtree.Node;

public class MBool extends MType {

	public String getName() {
		return "bool";
	}

	public int getSize() {
		return 4;
	}

	public boolean isAssignable(MType target) {
		if (target instanceof MBool)
			return true;
		else
			return false;
	}

}
