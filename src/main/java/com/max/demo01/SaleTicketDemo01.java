package com.max.demo01;

//基本的买票


/**
 * 真正的多线程开发，公司中开发
 * 线程就是一个单独的资源类，没有任何附属的操作
 * 1.属性
 * 2.方法
 */
public class SaleTicketDemo01 {

    public static void main(String[] args){

        final Ticket ticket = new Ticket();

        new Thread(()->{
            for (int i = 0;i <40;i++){
            ticket.sale();
            }
        },"A").start();

        new Thread(()->{
            for (int i = 0;i <40;i++){
                ticket.sale();
            }
        },"B").start();

        new Thread(()->{
            for (int i = 0;i <40;i++){
                ticket.sale();
            }
        },"C").start();
    }

}

//资源类
class Ticket{

    private int number = 30;

    /**
     * synchronized 锁的是对象和class文件
     */

     public synchronized void sale(){

        if(number > 0){
            System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "，剩余" + number);
        }

    }




}
