package com.max.demo01;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SaleTicketDemo02 {
    public static void main(String[] args){

        Ticket2 ticket2 = new Ticket2();

        new Thread(()->{
            for (int i = 0;i <40;i++)
                ticket2.sale();

        },"A").start();

        new Thread(()->{
            for (int i = 0;i <40;i++)
                ticket2.sale();

        },"B").start();

        new Thread(()->{
            for (int i = 0;i <40;i++)
                ticket2.sale();
        },"C").start();
    }
}

//资源类

/**
 * Lock三部曲
 * new ReentrantLock()
 * lock()加锁
 * unlock()解锁
 */
class Ticket2{

    private int number = 30;

    Lock l = new ReentrantLock();



    public  void sale(){

        //尝试着获取锁
        l.tryLock();

        //加锁
        l.lock();

        try{
            //业务代码
        if(number > 0){
            System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "，剩余" + number);
        }
        }catch (Exception e){
            //解锁
            l.unlock();
        }

    }
}
