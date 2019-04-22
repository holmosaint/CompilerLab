class Test{
    public static void main(String[] args){
        System.out.println(new C().call(new B()));
    }
}

class C {
    public int call(A a) {
        return a.func();
    }
}

class A {
    public int func() {
        return 10;
    }
}

class B extends A {
    public int func() {
        return 2;
    }
}
