package com.max.forkjoin;

import java.util.stream.LongStream;

public class Test1 {

    public static void main(String[] args) {

//        test1();//5992 500000000500000000

        test2();//4734 500000000500000000
//        test3();//500000000500000000 281
    }

    public static void test1(){

        long start =  System.currentTimeMillis();
        Long sum = 0L;
        for(Long i = 0L;i <= 1000000000L; i ++){

            sum +=i;
        }
        long end = System.currentTimeMillis();

        System.out.println("sum = "+ sum +",时间： " + (end-start));
    }

    public static void test2(){

        long start =  System.currentTimeMillis();

        ForkJoinDemo forkJoinDemo = new ForkJoinDemo(0L,1000000000L);

        Long sum = forkJoinDemo.compute();

        long end = System.currentTimeMillis();

        System.out.println("sum = "+ sum +",时间： " + (end-start));


    }

    public static void test3(){
        long start =  System.currentTimeMillis();

        //reduce前面的参数是累积量意思就是本来就有这些，后面是往上加的
        long sum = LongStream.rangeClosed(0L,1000000000L).parallel().reduce(0L,Long::sum);

        long end = System.currentTimeMillis();

        System.out.println("sum = "+ sum +",时间： " + (end-start));
    }

}
