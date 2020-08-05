package com.max.unsafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MapTest {

    public static void main(String[] args){
       //工作中不使用线程
        /**
         * loadFactor 默认的加载因子 0.75
         * 初始化容量 16
         */
//        Map<String,String> map = new HashMap<>();
//        Map<String,String> map = Collections.synchronizedMap(new HashMap<>());
        Map<String,String> map = new ConcurrentHashMap<>();
        for(int i=0;i< 30;i++){
            new Thread(()->{map.put(Thread.currentThread().getName(), UUID.randomUUID().toString());
            System.out.println(map);}).start();
        }
    }
}
