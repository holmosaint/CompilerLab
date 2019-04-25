class not_expression{
    public static void main(String[] a){
		boolean s;
		s = new Start().not();
		if(s) {
			System.out.println(15);
		}
		else {
			System.out.println(16);
		}
    }
}

class Start {
	boolean a;
	public boolean not() {
		a = true;
		return !a;
	}
}

