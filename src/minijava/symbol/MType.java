package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;

public abstract class MType {	
	abstract public String getName();
	abstract public int getSize();
	// TODO: Assignable check in case of unknown variable
	abstract public boolean isAssignable(MType target);
}
