package com.max.bq;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws InterruptedException {

        //抛出异常
//        test1();

        //不抛出异常
//        test2();

        //阻塞等待 当添加元素因为会出现一直阻塞 所以抛出异常
//        test3();

        //等待超时
        test4();

    }
    /**
     * 抛出异常
     */
    public static void test1(){

        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue<>(3);

        System.out.println(arrayBlockingQueue.add("a"));
        System.out.println(arrayBlockingQueue.add("b"));
        System.out.println(arrayBlockingQueue.add("c"));

        //检测队首元素
        System.out.println(arrayBlockingQueue.element());

        //当队列满的时候会发生异常 Queue full
//        System.out.println(arrayBlockingQueue.add("d"));


        System.out.println(arrayBlockingQueue.remove());
        System.out.println(arrayBlockingQueue.remove());
        System.out.println(arrayBlockingQueue.remove());
//        //当队列为空时继续移除异常 java.util.NoSuchElementException
        System.out.println(arrayBlockingQueue.remove());

    }

    /**
     * 不抛出异常
     */

    public static void test2(){

        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue<>(3);

        System.out.println(arrayBlockingQueue.offer("a"));
        System.out.println(arrayBlockingQueue.offer("b"));
        System.out.println(arrayBlockingQueue.offer("c"));
        //此时队列以满，此时不抛出异常，然后返回false
        System.out.println(arrayBlockingQueue.offer("d"));

        //检测队首元素
        System.out.println(arrayBlockingQueue.peek());

        System.out.println(arrayBlockingQueue.poll());
        System.out.println(arrayBlockingQueue.poll());
        System.out.println(arrayBlockingQueue.poll());
        //此时队列以空，不抛出异常，返回值为null
        System.out.println(arrayBlockingQueue.poll());

    }

    /**
     * 阻塞等待（一直阻塞）
     */
    public static void test3() throws InterruptedException {

        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);

        //一直阻塞
        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");
        //此时队列满了，会出现一直阻塞等待的现象
        blockingQueue.put("d");

        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());

        //没有这元素会一直等待 出现阻塞的现象
        System.out.println(blockingQueue.take());

    }

    public static void test4() throws InterruptedException {

        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);

        blockingQueue.offer("a");
        blockingQueue.offer("a");
        blockingQueue.offer("a");
        //当出现阻塞时，等待两秒就退出
        blockingQueue.offer("d", 3,TimeUnit.SECONDS);

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        blockingQueue.poll(5, TimeUnit.SECONDS);

    }

}
