package minijava.symbol;

import minijava.syntaxtree.*;

public abstract class MType {
	protected String name_;
	protected int size_;
	
	abstract public String getName_();
	abstract public int getSize_();
	abstract public boolean isAssignable(MType target, Node n);
}
