class check_not {
  public static void main(String[] a) {
    Start s;
	s = new Start();
	System.out.println(s.start());
  }
}
class Start {
  int []c;
  public int start() {
	  boolean bb;
	  int a;
	  int b;
	  a = 5;
	  b = 10;
	  bb = true;
	  if(bb)
		  System.out.println(a);
	  else
		  System.out.println(b);
	  return 0;
  }
}