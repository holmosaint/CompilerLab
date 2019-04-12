package minijava.symbol;

import java.io.*;
import java.util.*;

import minijava.MiniJavaParser;
import minijava.syntaxtree.*;
import minijava.typecheck.*;
import util.ErrorHandler;
import minijava.*;

// MRoot maintains the global information about the syntaxtree
public class SymbolTable {
	// attributes
	private static Goal root_;
	private static String file_name_;
	private static File file_;
	// list of MClass
	private static MClass main_class_ = null; // main class
	// TODO: Add MInt MBool MArray to the class_list_
	private static HashMap<String, MType> type_map_ = new HashMap<String, MType>(); // class list
	private static ArrayList<MClass> class_list_ = new ArrayList<MClass>();
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
		// 0.Initialize the SymbolTable
		file_name_ = file.getName();
		file_ = file;
		type_map_.put("boolean", new MBool());
		type_map_.put("int", new MInt());
		type_map_.put("array", new MArray());
		// classes.clear();
		// bin_classes.put("String", String);
		
		// 1.Initialize the parser
		// System.out.println("Initialize the parser...");
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
		// System.out.println("Parse and build syntaxtree...");
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
			type_map_.put(c.getName(), c);
			class_list_.add(c);
			
			if(!file_name_.equals(c.getName() + ".java")) {
				ErrorHandler.errorPrint("The main class name is not identical to the file name! " + 
										"Get main class: " + c.getName() + " while the file name is " + file_name_ + "\n");
			}
		}
		else {
			ErrorHandler.errorPrint("Too many main classes! Expect one but get two: " + 
									main_class_.getName() + " and " + c.getName() + ".\n");
		}
	}

	public static void addClass(MClass c) {
		if (type_map_.containsKey(c.getName())) {
			ErrorHandler.errorPrint("Get duplicate definition of class: " + c.getName() + "\n");
		}
		// System.out.println("Add class " + c.getName() + " to list");
		type_map_.put(c.getName(), c);
		class_list_.add(c);
	}
	
	public static void buildClass() {
		root_.accept(new ClassTreeBuilder(file_name_));
		// register the class into the class list
		for (MClass mclass : class_list_) {
			mclass.registerFather();
		}
		for (MClass mclass : class_list_) {
			mclass.fillBack();
		}
		for (MClass mclass : class_list_) {
			mclass.register();
		}
	}
	
	public static MType getType(Type t) {
		MType type = null;
		switch (t.f0.which) {
		case 0:
			// ArrayType
			type = type_map_.get("array");
			break;
		case 1:
			// BooleanType
			type = type_map_.get("boolean");
			break;
		case 2:
			// IntegerType
			type = type_map_.get("int");
			break;
		case 3:
			// Identifier (User-defined class)
			type = new MUndefined(((Identifier)t.f0.choice).f0.toString());
			break;
		default:
			ErrorHandler.errorPrint("Uknown variable type");
		}
		
		return type;
	}
	
	public static MType getType(String type_name) {
		return type_map_.get(type_name);
	}
	
	
	public static boolean parseVar(NodeListOptional var_list, HashMap<String, MVar> vars_) {
		for (Node node : var_list.nodes) {
			MVar var = new MVar(node);
			if (vars_.containsKey(var.getName())) {
				ErrorHandler.errorPrint("Duplicate definition of variable " + var.getName());
			} else {
				vars_.put(var.getName(), var);
			}
		}
		return true;
	}

	public static MClass queryClass(String className) {
		if (!type_map_.containsKey(className)) {
			ErrorHandler.errorPrint("Use undefined class " + className);
		}
		return (MClass) type_map_.get(className);
	}
	
}
