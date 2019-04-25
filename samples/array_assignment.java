class array_assignment {
  public static void main(String[] a) {
    int []c;
	Start s;
	s = new Start();
	c = new int [10];
	c[0] = 1;
	System.out.println(c[0]);
	c[2] = s.start();
  }
}
class Start {
  int []c;
  public int start() {
	  c = new int [10];
	  c[1] = 10;
	  System.out.println(c[1]);
	  return c[1];
  }
}