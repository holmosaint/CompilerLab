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
		File in_file = new File(src_file);
		File out_file = new File(dst_file);
		OutputStream kanga_file = null;
		String kanga_code;

		if (!in_file.isFile()) {
			ErrorHandler.errorPrint(src_file + "is not a file");
		}
		// Build syntaxtree for spiglet code
		SpigletVisitor.accept(in_file);
		System.out.println("Compiling " + src_file);
		try {
			SpigletParser.Goal().accept(new ClassTreeBuilder());
			// SymbolTable.printInfo();
			// Code.finish();
		} catch (ParseException e) {
			ErrorHandler.errorPrint(e.getMessage());
		}
		kanga_code = SymbolTable.toKange();
		if(!out_file.exists()) {
			try {
				out_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			kanga_file = new FileOutputStream(out_file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			kanga_file.write(kanga_code.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
}
