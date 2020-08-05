package com.max.lock8;

import java.util.concurrent.TimeUnit;

public class Test1 {

    public static void main(String[] args) throws InterruptedException {
        Phone phone = new Phone();
        new Thread(()->{phone.sendSms();},"A").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(()->{phone.call();},"B").start();
    }

}
class Phone{
    public synchronized void sendSms(){

        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发消息");
    }

    public synchronized void call(){
        System.out.println("打电话");
    }
}
