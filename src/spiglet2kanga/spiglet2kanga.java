package spiglet2kanga;

import java.io.*;
import spiglet.*;
import spiglet.symbol.SymbolTable;
import util.ErrorHandler;

class SpigletVisitor {
	public static void accept(final File file) {
		try {
			InputStream in_stream = new FileInputStream(file);
			new SpigletParser(in_stream);
		} catch (FileNotFoundException e) {
			ErrorHandler.errorPrint(e.getMessage());
		}
	}
}

public class spiglet2kanga {
	// a0-a3: 存放向子函数传递的参数
	// t0-t9: 存放临时运算结果，在发生函数调用时不必保存它们的内容
	// s0-s7: 存放局部变量，在发生函数调用时一般要保存它们的内容
	// v0-v1: v0 存放子函数返回结果；v0、v1还可用于表达式求值，从栈中加载
	private static String registers[] = {"s0", "s1", "s2", "s3", "s4", "s5", 
										 "s6", "s7", "t0", "t1", "t2", "t3", 
										 "t4", "t5", "t6", "t7", "t8", "t9", 
										 "a0", "a1", "a2", "a3", "v0", "v1"};
	public static void compile(String src_file, String dst_file) {
		File file = new File(src_file);
		if (!file.isFile()) {
			ErrorHandler.errorPrint(src_file + "is not a file");
		}
		// Build syntaxtree for spiglet code
		SpigletVisitor.accept(file);
		System.out.println("Compiling " + src_file);
		try {
			// Code.init(dst_file);
			SpigletParser.Goal().accept(new ClassTreeBuilder());
			// SymbolTable.printInfo();
			// Code.finish();
		} catch (ParseException e) {
			ErrorHandler.errorPrint(e.getMessage());
		}
		System.out.println("Done");
	}
}
