package com.max.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A执行完调用B，B执行完调用C，C执行完调用A
 * 使用了三个监视器来监视三个线程
 */
public class C {

    public static void main(String[] args){

        Data3 data = new Data3();

        new Thread(()->{for(int i= 0;i < 10; i++) data.printA();},"A").start();
        new Thread(()->{for(int i= 0;i < 10; i++) data.printB();},"B").start();
        new Thread(()->{for(int i= 0;i < 10; i++) data.printC();},"C").start();

    }

}

class Data3{

    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();
    private int number = 1;

    public void printA(){

        lock.lock();
        try {

            while(number != 1){
                condition1.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>AAAAA");

            condition2.signal();
            number = 2;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }
    public void printB(){
        lock.lock();
        try {
            while(number != 2){
                condition2.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>BBBBB");

            condition3.signal();
            number = 3;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void printC(){
        lock.lock();
        try {
            while(number != 3){
                condition3.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>CCCCC");

            condition1.signal();
            number = 1;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }




}