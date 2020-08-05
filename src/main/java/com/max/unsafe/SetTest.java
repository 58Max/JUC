package com.max.unsafe;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class SetTest {

    public static void main(String[] args){
        //java.util.ConcurrentModificationException
        /**
         * 同List一样
         */

//        Set<String> set = new HashSet<>();
//          Set<String> set = Collections.synchronizedSet(new HashSet<>());
        Set<String> set =new CopyOnWriteArraySet<>();

        for(int i =0 ;i < 100 ;i++){
            new Thread(()->{
                set.add(UUID.randomUUID().toString().substring(1,5));
                System.out.println(set);
            }).start();
        }

    }
}
