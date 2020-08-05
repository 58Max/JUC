package com.max.bq;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueDemo {

    public static void main(String[] args) {

        BlockingQueue<String> blockingDeque = new SynchronousQueue<String>();

        new Thread(()->{
            try{
            System.out.println(Thread.currentThread().getName() + " put 1");
            blockingDeque.put("1");
            System.out.println(Thread.currentThread().getName() + " put 2");
            blockingDeque.put("2");
            System.out.println(Thread.currentThread().getName() + " put 3");
            blockingDeque.put("3");
            }catch (Exception e){
                e.printStackTrace();
            }
        },"t1").start();


        new Thread(()->{

            try{
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " = " +blockingDeque.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " = " +blockingDeque.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " = " +blockingDeque.take());
            }catch (Exception e){
                e.printStackTrace();
            }
        },"t2").start();
    }
}
