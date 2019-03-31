package util;

public class ErrorHandler {
	public static void errorPrint(String errorMsg) {
		System.out.println(errorMsg);
		System.out.println("Typecheck error");
		System.exit(1);
	}
}