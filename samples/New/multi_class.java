class multi_class{
    public static void main(String[] a){
		Start s1;
		Start s2;
		boolean s;
		int a;
		a = 1;
		s1 = new Start();
		s2 = new Start();
		s = s1.not();
		if(s) {
			System.out.println(15);
		}
		else {
			System.out.println(16);
		}
		s = s2.not();
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

