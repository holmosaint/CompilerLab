class scope_conflict {
  public static void main(String[] a) {
    System.out.println(new Test().start());
  }
}

class Test {

  Test test;
  int j;

  public int start() {
    j = test.next(true);
    return 0;
  }
  public int next(boolean i) {
     int i;
    return 0;
  }
}