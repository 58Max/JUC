package com.max.pool;

import java.util.concurrent.*;

public class Demo2 {

    public static void main(String[] args) {

        /**
         * 自定义线程池
         * corePoolSize:核心线程数量
         * maximumPoolSize: 最大线程数量
         * keepAliveTime 超时了多久就会释放
         * TimeUnit 超时时间的单位
         * BlockQueue 阻塞队列
         * 线程工厂
         * 拒绝策略
         */
        ExecutorService threadPool = new ThreadPoolExecutor(2,5,3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()//
                );
        try{
            //最大承载等于队列数加最大线程数
            for(int i = 0; i < 4;i++){
                //使用了线程池之后，使用线程池来创建线程
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " ok" );
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            //线程池用完。程序结束。关闭线程池
            threadPool.shutdown();
        }


    }

}
