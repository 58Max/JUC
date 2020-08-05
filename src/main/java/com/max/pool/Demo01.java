package com.max.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo01 {
    /**
     * Executors
     * 工具类 3大方法
     * 使用了线程池之后，使用线程池创建线程
     * @param args
     */
    public static void main(String[] args) {
//        ExecutorService threadPoll = (ExecutorService) Executors.newSingleThreadExecutor();//单个线程
//        ExecutorService threadPoll = Executors.newFixedThreadPool(5);//创建一个固定的线程池的大小
        ExecutorService threadPoll = (ExecutorService) Executors.newCachedThreadPool();//可伸缩的线程池

        try{
            for(int i = 0; i < 10;i++){
                //使用了线程池之后，使用线程池来创建线程
                threadPoll.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " ok" );
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            //线程池用完。程序结束。关闭线程池
            threadPoll.shutdown();
        }


    }
}
