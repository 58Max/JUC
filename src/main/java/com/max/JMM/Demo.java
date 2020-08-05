package com.max.JMM;

import java.util.concurrent.TimeUnit;


/**
 * 我们需要让线程知道num中的值发生了变化
 */
public class Demo {

    /**
     * volatile 保证可见性
     */
    private volatile static int num =0 ;

    public static void main(String[] args) {



        new Thread(()->{
            while (num == 0){

                System.out.println("======================");
            }
        }).start();

        try{
            TimeUnit.SECONDS.sleep(1);
        }catch (Exception e){
            e.printStackTrace();
        }

        num = 1;
        System.out.println(num);
    }
}
