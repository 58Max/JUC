package com.max.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        MyThread myThread = new MyThread();
        FutureTask futureTask = new FutureTask<>(myThread);

        //如何使用Thread启动Callable
        //结果会有缓存
        new Thread(futureTask,"A").start();
        //get操作可能会产生阻塞 或者通过异步通信来处理
        String msg = (String)futureTask.get();

    }

}
 class MyThread implements Callable<String>{

     @Override
     public String call() {

         System.out.println("call");

         return "123456";

     }
 }
