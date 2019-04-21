import minijava.typecheck.*;

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
			}
			
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
}