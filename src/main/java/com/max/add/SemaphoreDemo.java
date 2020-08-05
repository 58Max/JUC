package com.max.add;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo {

    public static void main(String[] args){

        //容纳线程数量 可以限流！！！
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i< 6; i++){

            new Thread(()-> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "抢到了停车位");
                    TimeUnit.MINUTES.sleep(1);
                    System.out.println(Thread.currentThread().getName() + "离开了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            },String.valueOf(i)).start();

        }

    }
}
