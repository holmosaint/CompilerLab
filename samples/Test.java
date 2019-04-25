class Test{
    public static void main(String[] args){
        B b;
        A a;
        b = new B();
        System.out.println(b.set());
        System.out.println(b.func());
        a = b;
        System.out.println(a.getb());
        System.out.println(b.getb());
    }
}

class A {
    int atta;
    int attb;
    public int func() {
        return atta + attb;
    }
    public int getb() {
        return attb;
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
