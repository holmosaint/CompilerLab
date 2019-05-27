package spiglet.symbol;

import spiglet.syntaxtree.*;
import util.ErrorHandler;

public class MSimpleExp {
	// 0->Temp
	// 1->IntegerLiteral
	// 2->Label
	private int which_;
	private int tmp_id_, integer_;
	private String label_ = null;
	
	public MSimpleExp() {
		which_ = 1;
		integer_ = 0;
	}
	public MSimpleExp(SimpleExp simple_exp) {
		which_ = simple_exp.f0.which;
		switch (which_) {
		case 0:
			// Temp
			// "TEMP" tmp_id_
			Temp temp = (Temp)simple_exp.f0.choice;
			tmp_id_ = Integer.parseInt(temp.f1.f0.toString());
			break;
		case 1:
			// IntegerLiteral
			// integer_
			IntegerLiteral literal = (IntegerLiteral)simple_exp.f0.choice;
			integer_ = Integer.parseInt((literal).f0.toString());
			break;
		case 2:
			// Label
			// label_
			label_ = ((Label)simple_exp.f0.choice).f0.toString();
			break;
		default:
			ErrorHandler.errorPrint("nmdwsm!");
		}
	}
	
	public int getUsedId() {
		if (which_ == 0) return tmp_id_;
		else return -1;
	}

	public String getInfo() {
		String res = "";
		switch (which_) {
		case 0:
			// Temp
			// "TEMP" tmp_id_
			res += "TEMP " + tmp_id_;
			break;
		case 1:
			// IntegerLiteral
			// integer_
			res += integer_;
			break;
		case 2:
			// Label
			// label_
			res += label_;
			break;
		}
		return res;
	}
}
