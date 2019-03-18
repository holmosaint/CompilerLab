package minijava.symbol;

import minijava.syntaxtree.*;

public abstract class MType {
	abstract public String getName_();
	abstract public int getSize_();
	abstract public boolean isAssignable(MType target, Node n);
}
