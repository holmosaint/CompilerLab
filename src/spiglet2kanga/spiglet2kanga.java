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
	// a0-a3: ������Ӻ������ݵĲ���
	// t0-t9: �����ʱ���������ڷ�����������ʱ���ر������ǵ�����
	// s0-s7: ��žֲ��������ڷ�����������ʱһ��Ҫ�������ǵ�����
	// v0-v1: v0 ����Ӻ������ؽ����v0��v1�������ڱ��ʽ��ֵ����ջ�м���
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
