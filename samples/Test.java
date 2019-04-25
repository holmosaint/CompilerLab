class Test{
    public static void main(String[] args){
        A a;
        a = new A();
        System.out.println(a.get());
    }
}
class A {
    int a;
    int get() {
        return a;
    }

}
