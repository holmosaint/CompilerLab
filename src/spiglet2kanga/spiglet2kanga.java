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
			SymbolTable.printInfo();
			// Code.finish();
		} catch (ParseException e) {
			ErrorHandler.errorPrint(e.getMessage());
		}
		System.out.println("Done");
	}
}
