class duplicate_method {
  public static void main(String[] a) {
    System.out.println(new Start().start());
  }
}
class Start {
  public int start() {
    return 0;
  }
  public int start(int i) {
    return 0;
  }
}