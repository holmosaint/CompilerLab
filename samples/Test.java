class Test{
    public static void main(String[] args){
        B b;
        A a;
        b = new B();
        System.out.println(b.set());
        System.out.println(b.func());
        a = b;
        System.out.println(a.func());
    }
}

class A {
    int atta;
    int attb;
    public int func() {
        return atta + attb;
    }
}

class B extends A {
    int attb;
    public int func() {
        return atta + attb;
    }
    public int set() {
        attb = 1;
        return 1;
    }
}
