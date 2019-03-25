package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;

public abstract class MType {
	public static ArrayList<String> type_list;
	
	abstract public String getName();
	abstract public int getSize();
	// TODO: Assignable check in case of unknown variable
	abstract public boolean isAssignable(MType target);
	
	public boolean Query(String query_type) {
		for (String type : type_list) {
			if (query_type.equals(type)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean Register(String new_type) {
		if (Query(new_type)) {
			return false;
		}
		type_list.add(new_type);
		return true;
	}
}
