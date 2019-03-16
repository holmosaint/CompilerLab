package minijava.symbol;

import java.io.*;
import java.util.*;

import minijava.MiniJavaParser;
import minijava.syntaxtree.*;
import minijava.typecheck.*;
import minijava.*;

// MRoot maintains the global information about the syntaxtree
public class MRoot {
	// attributes
	private static Goal root_;
	private static String file_name_;
	private static File file_;
	// list of MClass
	
	// unique MType
	
	private static boolean first_time_ = true;
	public static boolean parse(final File file) {
		// 0.Initialize the MRoot
		file_name_ = file.getName();
		file_ = file;
		// classes.clear();
		// bin_classes.put("String", String);
		// main_class = null;
		
		// 1.Initialize the parser
		System.out.println("Initialize the parser");
		try {
			InputStream input_stream = new FileInputStream(file);
			if (first_time_) {
				new MiniJavaParser(input_stream);
				first_time_ = false;
			} else {
				MiniJavaParser.ReInit(input_stream);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		// 2.Start parsing
		System.out.println("Parse and build syntaxtree");
		try {
			root_ = MiniJavaParser.Goal();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	public static void buildClass() {
		root_.accept(new ClassTreeBuilder(file_name_));
	}
	public static void buildScope() {
		
	}
}
