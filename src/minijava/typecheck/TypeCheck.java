package minijava.typecheck;

import java.io.*;
import minijava.*;
import minijava.symbol.*;


public abstract class TypeCheck {
    public static boolean check(String file_name) {
        File file = new File(file_name);
        if (!file.isFile()) {
            System.out.println(file_name + " is not a file");
            return false;
        }

        return true;

    }
}