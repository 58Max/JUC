package com.max.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class B {

    public static void main(String[] args){

        Data2 data = new Data2();

        new Thread(()->{
            for(int i = 0;i < 10;i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();

        new Thread(()->{
            for(int i = 0;i < 10;i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"B").start();
        new Thread(()->{
            for(int i = 0;i < 10;i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"C").start();
        new Thread(()->{
            for(int i = 0;i < 10;i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"D").start();

    }

}

//资源类
class Data2{

    private int number = 0;

    //定义一个Lock锁
    Lock lock = new ReentrantLock();

    Condition  condition = lock.newCondition();

//    condition.await(); 等待
//    condition.signalAll();唤醒全部


    //+1

    public void increment() throws InterruptedException {

        //上锁
        lock.lock();
        try {
            while (number != 0) {
                //等待
                condition.await();
            }

            number++;

            System.out.println(Thread.currentThread().getName() + " => " + number);
            //通知其他线程
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();//解锁
        }

    }


    //-1
    public  void decrement() throws InterruptedException {

        //上锁
        lock.lock();
        try {
            while (number == 0) {
                //等待
                condition.await();
            }
            number--;

            System.out.println(Thread.currentThread().getName() + " => " + number);

            //通知其他线程
            condition.signalAll();
        }catch (Exception e){
           e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
