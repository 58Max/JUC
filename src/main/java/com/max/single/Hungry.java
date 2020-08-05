package com.max.single;

/**
 * 饿汉式
 * 可能会浪费空间
 */
public class Hungry {


    private Hungry() {}


    private final  static Hungry HUNGRY = new Hungry();
    public static Hungry getInstance(){
        return HUNGRY;
    }

}
