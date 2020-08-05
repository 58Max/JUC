package com.max.add;

import com.max.pc.C;

import java.util.concurrent.CountDownLatch;

//计数器
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {

        //当存在必须要执行任务的时候使用
        CountDownLatch countDownLatch =  new CountDownLatch(6);

        for(int i = 0; i< 6;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + " GO OUT");
            },String.valueOf(i)).start();

            countDownLatch.countDown();
        }

        //等待计数器归0，再往下执行
        countDownLatch.await();

        System.out.println("Close Door");



    }

}
