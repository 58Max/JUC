package com.max.lock8;

import java.util.concurrent.TimeUnit;

public class Test2  {

    public static void main(String[] args) throws InterruptedException {
        Phone2 phone = new Phone2();
        Phone2 phone1 = new Phone2();

        new Thread(()->{phone.sendSms();},"A").start();

        TimeUnit.SECONDS.sleep(1);

        phone1.hello();

        new Thread(()->{phone1.call();},"B").start();
    }

}
class Phone2{
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
    public void hello(){
        System.out.println("Hello");
    }
}