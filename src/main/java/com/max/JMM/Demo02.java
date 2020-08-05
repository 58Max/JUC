package com.max.JMM;

import java.util.concurrent.atomic.AtomicInteger;

public class Demo02 {

    /**
     * volatile 不保证原子性
     */
//    private volatile static int num = 0;

    private  volatile static AtomicInteger num1 = new AtomicInteger();

    public  static void add(){
        num1.getAndIncrement();
    }
    public static void main(String[] args) {

        for(int i = 0;i< 20;i++){
            new Thread(()->{
                for(int j = 0; j< 1000;j++){
                    add();
                }

            }).start();
        }

        while(Thread.activeCount()>2){
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + " " +num1);
    }
}
