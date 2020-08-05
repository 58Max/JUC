package com.max.add;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {


    public static void main(String[] args){
        /**
         * 集齐7个龙珠召唤神龙
         */
        //召唤龙珠的线程

         CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
             System.out.println("召唤神龙成功");});

        for(int i = 0; i < 7 ;i++){

            final int temp = i;

            new Thread(()->{System.out.println(Thread.currentThread().getName() + "收集了" + temp + "颗龙珠");
            try {
                cyclicBarrier.await();//等待
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            }).start();


        }

    }


}
