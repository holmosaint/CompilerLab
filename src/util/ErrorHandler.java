package util;

public class ErrorHandler {
	public static void errorPrint(String errorMsg) {
		System.out.println(errorMsg);
		Exception e = new Exception("");
		e.printStackTrace();
		System.out.println("Typecheck error");
		System.exit(1);
	}
}