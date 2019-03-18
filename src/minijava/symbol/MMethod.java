package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;
import minijava.typecheck.*;

public class MMethod {
	
	private MType ret_type_;
	private Identifier name_;
	private ArrayList<MVar> paras_;
	
	private MBlock body_;
	private MClass owner_;
	
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
