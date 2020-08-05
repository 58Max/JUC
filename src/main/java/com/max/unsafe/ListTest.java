package com.max.unsafe;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * java.util.ConcurrentModificationException
 * 并发修改异常
 */
public class ListTest {

    public static void main(String[] args){

        //并发下ArrayList不安全的
        /**
         * 解决方案：
         *  1.Vector
         *  2.Collections工具类的转换  List<String> list =  Collections.synchronizedList(new ArrayList<>());
         *  3.List<String> list = new CopyOnWriteArrayList<>(new ArrayList<>());
         * Vector使用的synchronized CopyOnWriteArrayList()使用的Lock锁
         */

//        List<String> list = new Vector<>();

//        List<String> list = new ArrayList<>();

//        List<String> list =  Collections.synchronizedList(new ArrayList<>());

        //CopyOnWriteArrayList<>()写入时复制 计算机程序设计领域的一种优化策略
        //多个线程调用时,list,读取的时候，固定的，写入（覆盖）
        //在写入的时候避免覆盖，造成数据问题
        List<String> list = new CopyOnWriteArrayList<>(new ArrayList<>());

        //

        for(int i = 0; i < 10; i++){
            new Thread(()->{list.add(UUID.randomUUID().toString().substring(0,5));
            System.out.println(list);},String.valueOf(i)).start();
        }

    }
}
