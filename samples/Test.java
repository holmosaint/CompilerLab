class Test{
    public static void main(String[] a){
        Fac fac;
        fac = new Fac();
        System.out.println(new Fac().ComputeFac(10));
    }
}

class Fac  {
    int[] b;
    Fac fac;
    public int ComputeFac(int num){
        int num_aux ;
        if (num < 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        return num_aux  + 1;
    }
}
