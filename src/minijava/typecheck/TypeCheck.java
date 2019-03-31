package minijava.typecheck;

import java.io.*;
import minijava.*;
import minijava.symbol.*;
import util.ErrorHandler;

public abstract class TypeCheck {

    public static boolean check(String file_name) {
        File file = new File(file_name);
        // System.out.println("Processing file " + file_name + "...");
        if (!file.isFile()) {
        	ErrorHandler.errorPrint(file_name + " is not a file!");
        }

        // judge whether is a java file
        if (!file_name.endsWith(".java")) {
        	ErrorHandler.errorPrint(file_name + " is not a java file!");
        }
        
        // Parse and build the syntaxtree
        if (!SymbolTable.parse(file)) {
        	return false;
        }
        
        // Build class tree
        SymbolTable.buildClass();
        
        return true;

    }
}