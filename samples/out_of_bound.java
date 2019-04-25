class out_of_bound {
  public static void main(String[] a) {
    System.out.println(new Start().start());
  }
}
class Start {
  int []a;
  public int start() {
    int b;
    b = 5 * 2;
    a = new int [10];
    a[b] = 2;
    System.out.println(a[b]);
    return 0;
  }
}