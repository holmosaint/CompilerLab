package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.typecheck.*;

public class MMethod extends MType {
	public MMethod() {
		
	}

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
