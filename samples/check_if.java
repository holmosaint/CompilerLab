class check_if {
  public static void main(String[] a) {
    Start s;
	s = new Start();
	System.out.println(s.start());
  }
}
class Start {
  int []c;
  public int start() {
	  int a;
	  int b;
	  a = 10;
	  b = 5;
	  if(a < b)
		  System.out.println(a);
	  else
		  System.out.println(b);
	  return 0;
  }
}