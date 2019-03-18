package minijava.symbol;

import java.util.*;
import minijava.syntaxtree.*;

public class MBlock {
	// attributes
	private MBlock block_father_ = null;
	private MClass class_father_ = null;
	private ArrayList<MBlock> children_ = new ArrayList<MBlock>();
	private HashMap<String, MVar> vars_ = new HashMap<String, MVar>();
	
	// Constructor
	public MBlock(MBlock block_father) {
		block_father_ = block_father;
	}
	public MBlock(MClass class_father) {
		class_father_ = class_father;
	}
	
	private void AddVar(MVar var) {
		
	}
	
	private boolean CheckVar(MVar var) {
		return false;
	}
	
	public void Register() {
		
	}
	
	private void AddBlock(MBlock block) {
		
	}
}
