package minijava.symbol;

import minijava.syntaxtree.*;

public class MArray extends MType {
	
	public String getName() {
		return "int[]";
	}
	
	public int getSize() {
		return 0;
	}
	
	public boolean isAssignable(MType target) {
		if (!(target instanceof MArray))
			return false;
		return true;
	}
}
