class  useless {
  public static void main(String[] a) {
    System.out.println(new A().start());
  }
}

class B extends A {}

class A {
  A a;
  B b;

  public int start() {
    b = new A();
    return 0;
  }
}