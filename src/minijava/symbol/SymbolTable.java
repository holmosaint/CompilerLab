package minijava.symbol;

import java.io.*;
import java.util.*;

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
	private static MClass main_class_ = null; // main class
	private static ArrayList<MClass> class_list_ = new ArrayList<MClass>(); // class list
		
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
		if(main_class_ == null) {
			main_class_ = c;
			if(!file_name_.equals(c.getName() + ".java")) {
				System.out.printf("The main class name is not identical to the file name! ");
				System.out.printf("Get main class: %s while the file name is %s\n", c.getName(), file_name_);
				System.exit(1);
			}
		}
		else {
			System.out.printf("Too many main classes! Expect one but get two: %s and %s.\n", 
								main_class_.getName(), c.getName());
			System.exit(1);
		}
	}

	public static void addClass(MClass c) {
		for(MClass e : class_list_) {
			if(e.getName() == c.getName()) {
				System.out.printf("Get duplicate definition of class: %s\n", e.getName());
				System.exit(1);
			}
		}
		class_list_.add(c);
	}
	
	public static void buildClass() {
		root_.accept(new ClassTreeBuilder(file_name_));

		// register the class into the class list
		for(MClass c : class_list_) {
			c.register();
		}
	}

	public static void buildScope() {
		
	}

	public static ArrayList<MClass> getClassList() {
		return class_list_;
	}
	
	public static MType getType(int which) {
		MType type;
		switch (which) {
		case 0:
			// ArrayType
			System.out.println("You decare Array");
			type = new MArray();
			break;
		case 1:
			// BooleanType
			System.out.println("You declare Bool");
			type = new MBool();
			break;
		case 2:
			// IntegerType
			System.out.println("You declare Int");
			type = new MInt();
			break;
		default:
			System.out.println("Uknown variable type");
			type = null;
			System.exit(1);
		}
		
		return type;
	}

	public static boolean parseVar(NodeListOptional var_list, HashMap<String, MVar> vars_) {
		for (Node node : var_list.nodes) {
			MVar var = new MVar(node);
			if (vars_.containsKey(var.getName())) {
				System.out.println("Duplicate definition of variable " + var.getName());
				return false;
			} else {
				vars_.put(var.getName(), var);
			}
		}
		return true;
	}
}
