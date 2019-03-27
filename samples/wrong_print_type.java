class wrong_print_type {
  public static void main(String[] a) {
    System.out.println(new Start().start());
  }
}
class Start {
  public int start() {
    System.out.println(true);
    return 0;
  }
}