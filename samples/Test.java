class Test{
    public static void main(String[] args){
        int[] a;
        A ca;
        B bb;
        C ccc;
        bb = new B();
        ca = new A();
        a = new int[4];
        a[0] = 1;
        a[1] = 2;
        a[2] = 3;
        a[3] = 4;
        System.out.println(new C().print(bb, a));
    }
}

class A {
    public int printArray(int[] a) {
        int b;
        int i;
        b = a.length;
        i = 0;
        while (i < b) {
            System.out.println(a[i]);
            i = i + 1;
        }
        return 1;
    }
}

class B extends A {
    public int printArra(int[] a) {
        int b;
        int i;
        int temp;
        b = a.length;
        i = b - 1;
        temp = 0 - 1;
        while (temp < i) {
            System.out.println(a[i]);
            i = i - 1;
        }
        return 1;
    }
}

class C {
    public int print(A a, int[] array) {
        return a.printArray(array);
    }
}