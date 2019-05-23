package spiglet.symbol;

import spiglet.syntaxtree.*;

public class MSimpleExp {
	// 0->Temp
	// 1->IntegerLiteral
	// 2->Label
	private int which_;
	private int tmp_id_, integer_;
	private String label_;
	public MSimpleExp(SimpleExp simple_exp) {
		which_ = simple_exp.f0.which;
		if (which_ == 0) {
			Temp temp = (Temp)simple_exp.f0.choice;
			tmp_id_ = Integer.parseInt(temp.f1.f0.toString());
		} else if (which_ == 1) {
			IntegerLiteral literal = (IntegerLiteral)simple_exp.f0.choice;
			integer_ = Integer.parseInt((literal).f0.toString());
		} else {
			label_ = ((Label)simple_exp.f0.choice).f0.toString();
		}
	}
}
