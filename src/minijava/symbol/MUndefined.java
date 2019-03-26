package minijava.symbol;

public class MUndefined extends MType {
	private String class_name_ = null;
	
	public MUndefined(String class_name) {
		class_name_ = class_name;
	}
	
	public String getClassName() {
		
		return class_name_;
	}
	
	public String getName() {
		return "Undefined";
	}

	public int getSize() {
		return 0;
	}

	public boolean isAssignable(MType target) {
		return false;
	}

}
