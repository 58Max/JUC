package com.max;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock
 * 独占锁（写锁） 只能被一个写线程所占用
 * 共享锁（读锁） 能被多个读线程占用
 *  读-读 可以共存
 *  读-写 不可以共存
 *  写-写  不可以共存
 */

public class ReadWriteLockDemo {

    public static void main(String[] args){

        MyCacheLock myCacheLock = new MyCacheLock();

        //写入
        for(int i = 1; i <= 5; i++){

            final int temp =i;

            new Thread(()->{
                myCacheLock.put(temp+"",temp+"");
            },String.valueOf(i)).start();

        }

        //读取
        for(int i = 1; i <= 5; i++){

            final int temp =i;

            new Thread(()->{
               System.out.println(myCacheLock.get(temp+""));
            },String.valueOf(i)).start();

        }

    }
}
/**
 * 加锁自定义缓存
 */
class MyCacheLock{

    //读写锁 更加细粒度的控制
    private ReentrantReadWriteLock reentrantReadWriteLock =  new ReentrantReadWriteLock();

    private volatile Map<String,Object> map = new HashMap<>();

    //存，写 加锁

    public void put(String key,Object value){

        reentrantReadWriteLock.writeLock().lock();

        try{

            System.out.println(Thread.currentThread().getName() + "写入" + key );
            map.put(key,value);
            System.out.println(Thread.currentThread().getName() + "写入完成"  );

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            reentrantReadWriteLock.writeLock().unlock();
        }



    }
    //去，读 加锁
    public Object get(String key){

        reentrantReadWriteLock.readLock().lock();

        try{
            System.out.println(Thread.currentThread().getName() + "读取" + key );

            return map.get(key);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            reentrantReadWriteLock.readLock().unlock();
        }

        return null;

    }
}
/**
 * 自定义缓存
 */
class MyCache{
    private volatile Map<String,Object> map = new HashMap<>();

    //存，写

    public void put(String key,Object value){

        System.out.println(Thread.currentThread().getName() + "写入" + key );
        map.put(key,value);
        System.out.println(Thread.currentThread().getName() + "写入完成"  );

    }
    //去，读
    public Object get(String key){

        System.out.println(Thread.currentThread().getName() + "读取" + key );

        return map.get(key);
    }
}