class  wrong_if_expr {
  public static void main(String[] a) {
    System.out.println(new Start().start());
  }
}
class Start {
  public int start() {
    int a;
    a = 1;
    if (a) {} else {}
    return 0;
  }
}