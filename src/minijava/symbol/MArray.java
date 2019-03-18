package minijava.symbol;

import minijava.syntaxtree.Node;

public class MArray extends MType {

	private MType element_type_;
	
	public String getName() {
		return element_type_.getName() + "[]";
	}
	
	public int getSize() {
		return 0;
	}

	public MType elementType() {
		return element_type_;
	}
	
	public boolean isAssignable(MType target) {
		if (!(target instanceof MArray))
			return false;
		return this.element_type_.isAssignable(((MArray) target).elementType());
	}
}
