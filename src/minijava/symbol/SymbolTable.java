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
	private static HashMap<String, MClass> class_list_ = new HashMap<String, MClass>(); // class list

	// unique MType
	private static boolean first_time_ = true;

	// reserved words
	private static HashSet<String> reserv_words = null;
	
	public static boolean isReserved(String name) {
		if (reserv_words == null) {
			reserv_words = new HashSet<String>();
			reserv_words.add("class");
			reserv_words.add("public");
			reserv_words.add("static");
			reserv_words.add("void");
			reserv_words.add("main");
			reserv_words.add("String");
			reserv_words.add("extends");
			reserv_words.add("return");
			reserv_words.add("int");
			reserv_words.add("boolean");
			reserv_words.add("if");
			reserv_words.add("while");
			reserv_words.add("System");
			reserv_words.add("out");
			reserv_words.add("println");
			reserv_words.add("true");
			reserv_words.add("false");
			reserv_words.add("this");
			reserv_words.add("new");
		}
		if (reserv_words.contains(name)) {
			System.out.println(name + " is a reserverd word!");
			return true;
		} else {
			return false;
		}
	}
	
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
			class_list_.put(c.getName(), c);
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
		if(class_list_.containsKey(c.getName())) {
			System.out.printf("Get duplicate definition of class: %s\n", c.getName());
			System.exit(1);
		}
		System.out.println("Add class " + c.getName() + " to list");
		class_list_.put(c.getName(), c);
	}
	
	public static void buildClass() {
		root_.accept(new ClassTreeBuilder(file_name_));
		// register the class into the class list
		for (String key : class_list_.keySet()) {
			class_list_.get(key).register();
		}
	}

	public static void buildScope() {
		
	}

	public static HashMap<String, MClass> getClassList() {
		return class_list_;
	}
	
	public static MType getType(Type t) {
		System.out.println("SymbolTabel.gettype: " + t.f0.which);
		MType type = null;
		switch (t.f0.which) {
		case 0:
			// ArrayType
			type = new MArray();
			break;
		case 1:
			// BooleanType
			type = new MBool();
			break;
		case 2:
			// IntegerType
			type = new MInt();
			break;
		case 3:
			// Identifier (User-defined class)
			type = new MUndefined(((Identifier)t.f0.choice).f0.toString());
			break;
		default:
			System.out.println("Uknown variable type ");
			System.exit(1);
		}
		
		return type;
	}
	
	public static boolean parseVar(NodeListOptional var_list, HashMap<String, MVar> vars_) {
		for (Node node : var_list.nodes) {
			MVar var = new MVar(node);
			if (vars_.containsKey(var.getName())) {
				System.out.print("Duplicate definition of variable " + var.getName() + " ");
				return false;
			} else {
				vars_.put(var.getName(), var);
			}
		}
		return true;
	}

	public static MClass queryClass(String className) {
		if (!class_list_.containsKey(className)) {
			System.out.println("Using of undefined class " + className);
			System.exit(1);
		}
		return class_list_.get(className);
	}
	
}
