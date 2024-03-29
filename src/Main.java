import minijava.typecheck.*;
import minijava2piglet.*;
import minijava2spiglet.*;
import spiglet2kanga.*;
import kanga2mips.*;

public class Main {
	public static void main(String args[]) {
		try {
			String file_path = "..\\samples\\TreeVisitor.kg";
			if (args.length > 0) {
				file_path = args[0];
			}
			if (file_path.endsWith(".java")) {
				boolean res = TypeCheck.check(file_path);
				if (res) {
					System.out.println("Typecheck OK!");
				} else {
					System.out.println("Typecheck error!");
					System.exit(1);
				}
				System.out.println("Converting minijava to piglet");
				minijava2piglet convert2piglet = new minijava2piglet(file_path);
				System.out.println("Converting minijava to spiglet");
				minijava2spiglet convert2spiglet = new minijava2spiglet(file_path);
			} else if (file_path.endsWith(".spg")) {
				spiglet2kanga.compile(file_path, file_path.replace(".spg", ".kg"));
			} else if (file_path.endsWith(".kg")) {
				kanga2mips.compile(file_path, file_path.replace(".kg", ".s"));
			}
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
}

