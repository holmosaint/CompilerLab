package minijava.symbol;

import java.io.*;
import java.util.*;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.javah.Util.Exit;

import minijava.MiniJavaParser;
import minijava.syntaxtree.*;
import minijava.typecheck.*;
import minijava.*;

// MRoot maintains the global information about the syntaxtree
public class SymbolTable {
	// attributes
	private static Goal root_;
	private static String file_name_;
	private static File file_;
	// list of MClass
	private static MClass main_class_; // main class
	private static ArrayList<MClass> class_list_; // class list
		
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
		System.out.println("Initialize the parser...");
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
		System.out.println("Parse and build syntaxtree...");
		try {
			root_ = MiniJavaParser.Goal();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public static void addMainClass(MClass c) {
		if(main_class_ != null) {
			main_class_ = c;
		}
		else {
			System.out.printf("Too many main classes! Expect one but get two: %s and %s.\n", 
								main_class_.getName(), c.getName());
			System.exit(1);
		}
	}

	public static void addClass(MClass c) {
		class_list_.add(c);
	}
	
	public static void buildClass() {
		root_.accept(new ClassTreeBuilder(file_name_));

		// register the class into the class list
		for(MClass c : class_list_) {
			c.Register();
		}

		// check the extension loop
		for(MClass c : class_list_) {
			MClass father = c.getFather();
			while(father != null) {
				if(father.getName() == c.getName()) {
					System.out.printf("Extension loop found in class: %s and %s\n",
										father.getName(), c.getName());
					System.exit(1);
				}
			}
		}
	}

	public static void buildScope() {
		
	}
}
