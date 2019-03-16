package minijava.symbol;

import minijava.syntaxtree.*;

public class MVar extends MType {

	public String getName_() {
		return null;
	}

	public int getSize_() {
		return 0;
	}

	public boolean isAssignable(MType target, Node n) {
		return false;
	}
	
}
