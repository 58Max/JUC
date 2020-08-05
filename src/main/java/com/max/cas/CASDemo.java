package com.max.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class CASDemo {


    /**
     * CAS是CPU的并发原语
     * @param args
     */
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(2020);

        /**
         * 期望 更新
         * public final boolean compareAndSet(int expect, int update) {
         *         return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
         *     }
         *     如果我期望的值达到了就更新否则就不更新
         */
//        atomicInteger.compareAndSet(2020,2021);
//        atomicInteger.getAndIncrement();
//        System.out.println(atomicInteger.get());

        AtomicStampedReference<Integer> integerAtomicStampedReference = new AtomicStampedReference<>(22, 1);

        new Thread(()->{

            int stamp = integerAtomicStampedReference.getStamp();
            System.out.println("a1=> " + stamp);
            try{
                TimeUnit.SECONDS.sleep(2);
            }catch(Exception e){
                e.printStackTrace();
            }

            System.out.println(integerAtomicStampedReference.compareAndSet(22,33,integerAtomicStampedReference.getStamp(),integerAtomicStampedReference.getStamp()+1));
            System.out.println("a2->" + integerAtomicStampedReference.getStamp());
            System.out.println(integerAtomicStampedReference.compareAndSet(33,22,integerAtomicStampedReference.getStamp(),integerAtomicStampedReference.getStamp()+1));
            System.out.println("a3->" + integerAtomicStampedReference.getStamp());

        },"a1").start();

        new Thread(()->{

            int stamp = integerAtomicStampedReference.getStamp();
            System.out.println("b1=> " + stamp);
            try{
                TimeUnit.SECONDS.sleep(2);
            }catch(Exception e){
                e.printStackTrace();
            }
            System.out.println(integerAtomicStampedReference.compareAndSet(22, 66, stamp, integerAtomicStampedReference.getStamp() + 1));
            System.out.println("b2->" + integerAtomicStampedReference.getStamp());

        },"b1").start();
    }
}
