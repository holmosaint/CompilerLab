import minijava.typecheck.*;
import minijava2piglet.*;
import minijava2spiglet.*;

public class Main {
	public static void main(String args[]) {
		try {
			String file_path = "..\\samples\\Test.java";
			if (args.length > 0) {
				file_path = args[0];
			}
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
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
}

