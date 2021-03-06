# JUC
这是学习并发编程时的笔记和代码
# *JUC编程*

## 1.什么是JUC?

 ==java.util.concurrent==



![image-20200720111818735](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200720111818735.png)




## 2.线程和进程

> 线程、进程

### 进

一个进程往往包含多个线程，至少包含一个

java默认有2个线程：main ，GC

### 线程

......

**java并不能开启线程！！！**

```java
public synchronized void start() {
    /**
     * This method is not invoked for the main method thread or "system"
     * group threads created/set up by the VM. Any new functionality added
     * to this method in the future may have to also be added to the VM.
     *
     * A zero status value corresponds to state "NEW".
     */
    if (threadStatus != 0)
        throw new IllegalThreadStateException();

    /* Notify the group that this thread is about to be started
     * so that it can be added to the group's list of threads
     * and the group's unstarted count can be decremented. */
    group.add(this);

    boolean started = false;
    try {
        start0();
        started = true;
    } finally {
        try {
            if (!started) {
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {
            /* do nothing. If start0 threw a Throwable then
              it will be passed up the call stack */
        }
    }
}

//调用本地的方法 ，底层的c++，java无法直接操作硬件
private native void start0();
```

> 并发、并行

### 并发

（多个线程操作同一个资源，涉及到时间片）单核

### 并行

（多核情况下，真正的同时执行）线程池

并发编程的本质：==充分利用cpu的资源==

### 线程的几个状态

```java
 public enum State {
        /**
         * Thread state for a thread which has not yet started.
         */
        NEW,
     //新生

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
        RUNNABLE,
     //运行

        /**
         * Thread state for a thread blocked waiting for a monitor lock.
         * A thread in the blocked state is waiting for a monitor lock
         * to enter a synchronized block/method or
         * reenter a synchronized block/method after calling
         * {@link Object#wait() Object.wait}.
         */
        BLOCKED,
     //阻塞

        /**
         * Thread state for a waiting thread.
         * A thread is in the waiting state due to calling one of the
         * following methods:
         * <ul>
         *   <li>{@link Object#wait() Object.wait} with no timeout</li>
         *   <li>{@link #join() Thread.join} with no timeout</li>
         *   <li>{@link LockSupport#park() LockSupport.park}</li>
         * </ul>
         *
         * <p>A thread in the waiting state is waiting for another thread to
         * perform a particular action.
         *
         * For example, a thread that has called <tt>Object.wait()</tt>
         * on an object is waiting for another thread to call
         * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
         * that object. A thread that has called <tt>Thread.join()</tt>
         * is waiting for a specified thread to terminate.
         */
        WAITING,
     //无期限的等

        /**
         * Thread state for a waiting thread with a specified waiting time.
         * A thread is in the timed waiting state due to calling one of
         * the following methods with a specified positive waiting time:
         * <ul>
         *   <li>{@link #sleep Thread.sleep}</li>
         *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
         *   <li>{@link #join(long) Thread.join} with timeout</li>
         *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
         *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
         * </ul>
         */
        TIMED_WAITING,
     //超时等待，有期限

        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         */
        TERMINATED;
     //终止
    }

```



#### wait、sleep的区别

##### 1.来自不同类

wait->Object

sleep->Thread(一般不使用) 

##### 2.关于锁的释放

wait会释放锁

sleep不会释放锁

##### 3.使用的范围不同

wait ：必须在同步代码块中使用

sleep：哪里都可以睡

##### 4.是否需要捕获异常

wait不需要捕获异常

sleep需要捕获异常





## 3.LOCK锁（重点）

> 传统synchronized



> Lock

### 实现类

![image-20200720140234264](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200720140234264.png)

![image-20200720140209843](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200720140209843.png)

![image-20200720140539409](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200720140539409.png)

公平锁：十分公平，必须按照先来后到

非公平锁：十分不公平，可以插队（十分公平）默认的



### synchronized和Lock的区别

====1.synchronized是一个关键字，lock是一个java类==

==2.synchronized无法判断锁的状态，Lock可以判断是否获取到了锁==

==3.synchronized 会自动释放锁，Lock必须手动释放锁！如果不释放锁，**死锁**==

==4.synchronized 线程1（获得锁阻塞），线程2（等待，一直等待），lock不一定会一值等待下去==

==5.synchronized 可重入锁，不可以中断，非公平；lock可重入锁，可以中断，可以自己设置==

==6.synchronized适合锁少量的代码同步问题，lock适合锁大量的同步代码==



## 4.生产者和消费者问题

> synchronized版本的

```java
class Data{

    private int number = 0;

    //+1

    public synchronized void increment() throws InterruptedException {
        if (number != 0){
            //等待
            this.wait();
        }

        number++;

        System.out.println(Thread.currentThread().getName() + " => " + number);
        //通知其他线程
        this.notify();

    }


    //-1
    public synchronized void decrement() throws InterruptedException {
        if(number == 0){
            //等待
            this.wait();
        }
        number--;

        System.out.println(Thread.currentThread().getName() + " => " + number);

        //通知其他线程
        this.notify();
    }
}
```

> JUC版本

```java
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
}                              vc vcc
```



> 问题当存在多个线程时 会存在虚假唤醒

![image-20200720145622395](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200720145622395.png)

解决方法：把if替换成while



### Condition 精准的通知唤醒

普通的不能够有序的执行

![image-20200720151943627](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200720151943627.png)



> Condition精准通知唤醒线程

```java
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
```

![image-20200720153111292](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200720153111292.png)



## 5.8锁现象

如何判断所得是谁！什么是锁！锁到底锁的是谁！

#### 深刻理解锁

```java
public class Test1 {

    public static void main(String[] args) throws InterruptedException {
        Phone phone = new Phone();
        new Thread(()->{phone.sendSms();},"A").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(()->{phone.call();},"B").start();
    }

}
class Phone{
    public static synchronized void sendSms(){

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
```



==这两个方法使用的是用一个锁，这里的synchronized锁的是对象，这两个方法谁先拿到锁水就会先执行，当在static方法前面加synchronized，锁的使整个字节码class文件==



## 6.集合不安全



### List不安全

```java
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * java.util.ConcurrentModificationException
 * 并发修改异常
 */
public class ListTest {

    public static void main(String[] args){

        //并发下ArrayList不安全的
        /**
         * 解决方案：
         *  1.Vector
         *  2.Collections工具类的转换  List<String> list =  Collections.synchronizedList(new ArrayList<>());
         *  3.List<String> list = new CopyOnWriteArrayList<>(new ArrayList<>());
         * Vector使用的synchronized CopyOnWriteArrayList()使用的Lock锁
         */

//        List<String> list = new Vector<>();

//        List<String> list = new ArrayList<>();

//        List<String> list =  Collections.synchronizedList(new ArrayList<>());

        //CopyOnWriteArrayList<>()写入时复制 计算机程序设计领域的一种优化策略
        //多个线程调用时,list,读取的时候，固定的，写入（覆盖）
        //在写入的时候避免覆盖，造成数据问题
        List<String> list = new CopyOnWriteArrayList<>(new ArrayList<>());

        //

        for(int i = 0; i < 10; i++){
            new Thread(()->{list.add(UUID.randomUUID().toString().substring(0,5));
            System.out.println(list);},String.valueOf(i)).start();
        }

    }
}
```



### Set不安全

```java
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
```

Set的底层是什么？

```java
public boolean add(E e) {
    return map.put(e, PRESENT)==null;
}

public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
PRESENT是一个常量值
```



### Map不安全

```JAVA
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MapTest {

    public static void main(String[] args){
       //工作中不使用线程
        /**
         * loadFactor 默认的加载因子 0.75
         * 初始化容量 16
         */
//        Map<String,String> map = new HashMap<>();
//        Map<String,String> map = Collections.synchronizedMap(new HashMap<>());
        Map<String,String> map = new ConcurrentHashMap<>();
        for(int i=0;i< 30;i++){
            new Thread(()->{map.put(Thread.currentThread().getName(), UUID.randomUUID().toString());
            System.out.println(map);}).start();
        }
    }
}
```



## 7.Callable

![image-20200722161708770](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200722161708770.png)

1.可以有返回值

2.可以返回异常

3.方法不同run()/call();

```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        MyThread myThread = new MyThread();
        FutureTask futureTask = new FutureTask<>(myThread)

        //如何使用Thread启动Callable
        //
        new Thread(futureTask,"A").start();
        //get操作可能会产生阻塞 或者通过异步通信来处理
        String msg = (String)futureTask.get();

    }

}
 class MyThread implements Callable<String>{

     @Override
     public String call() {

         System.out.println("call");

         return "123456";

     }
 }
```

细节

1.结果有缓存

2.在代码块中可能会很耗时产生阻塞，需要等待



## 8.常用辅助类（必须掌握）



### 8.1CountDownLatch

==****减法计数器****==



![image-20200722161750051](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200722161750051.png)

### 8.2CyclicBarrier

==****加法计数器****==

![image-20200722180537284](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200722180537284.png)

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {


    public static void main(String[] args){
        /**
         * 集齐7个龙珠召唤神龙
         */
        //召唤龙珠的线程

         CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
             System.out.println("召唤神龙成功");});

        for(int i = 0; i < 7 ;i++){

            final int temp = i;

            new Thread(()->{System.out.println(Thread.currentThread().getName() + "收集了" + temp + "颗龙珠");
            try {
                cyclicBarrier.await();//等待
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            }).start();


        }

    }


}
```

### 8.3Semaphore

![image-20200722180958737](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200722180958737.png)

```java
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo {

    public static void main(String[] args){

        //容纳线程数量 可以限流！！！
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i< 6; i++){

            new Thread(()-> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "抢到了停车位");
                    TimeUnit.MINUTES.sleep(1);
                    System.out.println(Thread.currentThread().getName() + "离开了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            },String.valueOf(i)).start();

        }

    }
}
```



原理：

`semaphore.acquire()`: 获得，假设已经满了就等待其他被释放为止

`semaphore.release()`：释放，会将当前的信号量释放+1；然后等待唤醒线程

作用：多个共享资源的互斥使用！并发限流，控制最大的线程数



## 9.ReadWriteLock



![image-20200722182659165](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200722182659165.png)



```java
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
```



## 10.BlockingQueue（单端）

阻塞

1.不得不阻塞

 写入：如果队列满了，就必须阻塞等待

 取：如果队列为空，就必须阻塞等待

![image-20200723094019790](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723094019790.png)

什么情况下会使用阻塞队列：多线程并发处理，线程池！

学会使用队列

添加，移除

4组API

| 方式         | 抛出异常  | 有返回值,不抛出异常 | 阻塞，等待 | 超时等待          |
| ------------ | --------- | ------------------- | ---------- | ----------------- |
| 添加         | add()     | offer()             | put()      | offer(,,)重载方法 |
| 移除         | remove()  | poll()              | take()     | poll(,)重载方法   |
| 判断队列队首 | element() | peek()              |            |                   |

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws InterruptedException {

        //抛出异常
//        test1();

        //不抛出异常
//        test2();

        //阻塞等待 当添加元素因为会出现一直阻塞 所以抛出异常
//        test3();

        //等待超时
        test4();

    }
    /**
     * 抛出异常
     */
    public static void test1(){

        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue<>(3);

        System.out.println(arrayBlockingQueue.add("a"));
        System.out.println(arrayBlockingQueue.add("b"));
        System.out.println(arrayBlockingQueue.add("c"));

        //检测队首元素
        System.out.println(arrayBlockingQueue.element());

        //当队列满的时候会发生异常 Queue full
//        System.out.println(arrayBlockingQueue.add("d"));


        System.out.println(arrayBlockingQueue.remove());
        System.out.println(arrayBlockingQueue.remove());
        System.out.println(arrayBlockingQueue.remove());
//        //当队列为空时继续移除异常 java.util.NoSuchElementException
        System.out.println(arrayBlockingQueue.remove());

    }

    /**
     * 不抛出异常
     */

    public static void test2(){

        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue<>(3);

        System.out.println(arrayBlockingQueue.offer("a"));
        System.out.println(arrayBlockingQueue.offer("b"));
        System.out.println(arrayBlockingQueue.offer("c"));
        //此时队列以满，此时不抛出异常，然后返回false
        System.out.println(arrayBlockingQueue.offer("d"));

        //检测队首元素
        System.out.println(arrayBlockingQueue.peek());

        System.out.println(arrayBlockingQueue.poll());
        System.out.println(arrayBlockingQueue.poll());
        System.out.println(arrayBlockingQueue.poll());
        //此时队列以空，不抛出异常，返回值为null
        System.out.println(arrayBlockingQueue.poll());

    }

    /**
     * 阻塞等待（一直阻塞）
     */
    public static void test3() throws InterruptedException {

        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);

        //一直阻塞
        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");
        //此时队列满了，会出现一直阻塞等待的现象
        blockingQueue.put("d");

        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());

        //没有这元素会一直等待 出现阻塞的现象
        System.out.println(blockingQueue.take());

    }

    public static void test4() throws InterruptedException {

        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);

        blockingQueue.offer("a");
        blockingQueue.offer("a");
        blockingQueue.offer("a");
        //当出现阻塞时，等待两秒就退出
        blockingQueue.offer("d", 3,TimeUnit.SECONDS);

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        blockingQueue.poll(5, TimeUnit.SECONDS);

    }

}
```



## 10. SynchronousQueue

没有容量，进去一个元素必须等待取出来之后，才能再往里面再放一个元素

![image-20200723103855308](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723103855308.png)

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueDemo {

    public static void main(String[] args) {

        BlockingQueue<String> blockingDeque = new SynchronousQueue<String>();

        new Thread(()->{
            try{
            System.out.println(Thread.currentThread().getName() + " put 1");
            blockingDeque.put("1");
            System.out.println(Thread.currentThread().getName() + " put 2");
            blockingDeque.put("2");
            System.out.println(Thread.currentThread().getName() + " put 3");
            blockingDeque.put("3");
            }catch (Exception e){
                e.printStackTrace();
            }
        },"t1").start();


        new Thread(()->{

            try{
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=" +blockingDeque.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=" +blockingDeque.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=" +blockingDeque.take());
            }catch (Exception e){
                e.printStackTrace();
            }
        },"t2").start();
    }
}
```



## 11.线程池（重点）

线程池：三大方法、7大参数、4种拒绝策略

> 池化技术

程序的运行，本质：占用系统资源！优化资源的使用！=》池化技术

线程池、连接池、内存池、对象池////。。。。。。

池化技术：事先准备好一些资源，有人要用，就来我这里来拿，用完之后还给我

线程池的好处：

1、降低资源的消耗

2、提高响应的速度

3、方便管理

==线程复用、可以控制最大并发数。管理线程==

> 线程池 : 三大方法

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo01 {
    /**
     * Executors
     * 工具类 3大方法
     * 使用了线程池之后，使用线程池创建线程
     * @param args
     */
    public static void main(String[] args) {
//        ExecutorService threadPoll = (ExecutorService) Executors.newSingleThreadExecutor();//单个线程
        ExecutorService threadPoll = Executors.newFixedThreadPool(5);//创建一个固定的线程池的大小
//        ExecutorService threadPoll = (ExecutorService) Executors.newCachedThreadPool();//可伸缩的线程池

        try{
            for(int i = 0; i < 10;i++){
                //使用了线程池之后，使用线程池来创建线程
                threadPoll.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " ok" );
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            //线程池用完。程序结束。关闭线程池
            threadPoll.shutdown();
        }


    }
}
```

> 7大参数

```java
 public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
 public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }
 public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }

本质：ThreadPoolExecutor()
    
     public ThreadPoolExecutor(int corePoolSize,    //核心线程池大小
                              int maximumPoolSize,  //最大核心线程池大小
                              long keepAliveTime,   //超时了没有人调用就会释放
                              TimeUnit unit,        //超时的单位
                              BlockingQueue<Runnable> workQueue, //阻塞队列
                              ThreadFactory threadFactory, //创建线程的线程工厂
                              RejectedExecutionHandler handler //拒绝处理策略
                              ) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.acc = System.getSecurityManager() == null ?
                null :
                AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
```

> 手动创建一个线程池

```java
import java.util.concurrent.*;

public class Demo2 {

    public static void main(String[] args) {

        /**
         * 自定义线程池
         * corePoolSize:核心线程数量
         * maximumPoolSize: 最大线程数量
         * keepAliveTime 超时了多久就会释放
         * TimeUnit 超时时间的单位
         * BlockQueue 阻塞队列
         * 线程工厂
         * 拒绝策略：分为4种
         */
        ExecutorService threadPool = new ThreadPoolExecutor(2,5,3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()//
                );
        try{
            //最大承载等于队列数加最大线程数
            for(int i = 0; i < 4;i++){
                //使用了线程池之后，使用线程池来创建线程
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " ok" );
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            //线程池用完。程序结束。关闭线程池
            threadPool.shutdown();
        }


    }

}
```



> 4种拒绝策略

```java
private static final RejectedExecutionHandler defaultHandler =
        new AbortPolicy();//默认策略

//当线程池满了以后就不在允许其他线程进入并且抛出异常
 public static class AbortPolicy implements RejectedExecutionHandler {
        /**
         * Creates an {@code AbortPolicy}.
         */
        public AbortPolicy() { }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() +
                                                 " rejected from " +
                                                 e.toString());
        }
    }

//队列满了，哪来的去哪里，让其他来执行
public static class CallerRunsPolicy implements RejectedExecutionHandler {
        /**
         * Creates a {@code CallerRunsPolicy}.
         */
        public CallerRunsPolicy() { }

        /**
         * Executes task r in the caller's thread, unless the executor
         * has been shut down, in which case the task is discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

//队列满了，把任务丢掉，不会抛出异常
 public static class DiscardPolicy implements RejectedExecutionHandler {
        /**
         * Creates a {@code DiscardPolicy}.
         */
        public DiscardPolicy() { }

        /**
         * Does nothing, which has the effect of discarding task r.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }

//队列满了，尝试和最早的竞争资源，竞争失败会被丢掉，不会抛出异常
  public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        /**
         * Creates a {@code DiscardOldestPolicy} for the given executor.
         */
        public DiscardOldestPolicy() { }

        /**
         * Obtains and ignores the next task that the executor
         * would otherwise execute, if one is immediately available,
         * and then retries execution of task r, unless the executor
         * is shut down, in which case task r is instead discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
```



池的最大线程数量如何设置

```java
Run.getRuntime().availableProcessors();
获取当前的电脑的核数
```

1.CPU 密集型 几核就是几个 可以保持效率最高

2.IO 密集型 判断你的程序中十分耗IO的线程的2倍

例： 程序有15个大型任务  io十分占用资源

## 12.四大函数式接口（重点必须掌握）



> 函数式接口 是由一个方法的接口

例：

```
@FunctionalInterface
public interface Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    public abstract void run();
}
```



> Function<T,R>函数式接口

![image-20200723155724662](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723155724662.png)

![image-20200723155941880](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723155941880.png)

```java
import java.util.function.Function;

public class Demo01 {

    public static void main(String[] args) {

        Function<String,String> function = (str)->{
            return str; 
        };

        System.out.println(function.apply("sss"));

    }
}
```



> Predicate 断定型函数接口



![image-20200723160337033](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723160337033.png)

![image-20200723160531264](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723160531264.png)

```java
import java.util.function.Predicate;

public class Demo02 {

    public static void main(String[] args) {

        Predicate<String> predicate = (str)->{
            return str.isEmpty();
        };

    }

}
```



> Consumer 消费型接口

![image-20200723160910097](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723160910097.png)



![image-20200723161100151](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723161100151.png)

```java
import java.util.function.Consumer;

public class Demo03 {

    public static void main(String[] args) {

        Consumer<String> consumer = (str)->{

            System.out.println(str);
        };
    }
}
```





> Supplier 供给型接口

![image-20200723161301745](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723161301745.png)

![image-20200723161345684](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723161345684.png)

```java
import java.util.function.Supplier;

public class Demo04 {

    public static void main(String[] args) {
        Supplier<String> stringSupplier = ()->{
            return "aaaa";
        };
    }
}
```



## 13.Stream流式计算

> 什么是Stream流式计算

大数据：存储加计算

存储：集合，MySql

![image-20200723165839659](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723165839659.png)

```java
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
public class User {
 
    //输出id是二的倍数年龄大于23岁且名字转为大写并且只输出一个

    private Integer id;
    private String name;
    private Integer age;

    public static void main(String[] args) {
        User u1 = new User(1,"a",11);
        User u2 = new User(2,"b",22);
        User u3 = new User(3,"c",33);
        User u4 = new User(4,"d",44);
        User u5 = new User(5,"e",11);

        List<User> list = Arrays.asList(u1,u2,u3,u4,u5);

        list.stream().filter(u->{ return u.id%2 == 0;}).filter(u->{return u.age>23;}).map(user -> user.name.toUpperCase()).limit(1).forEach(System.out::println);

    }
}
```



## 14.ForkJoin

并行执行任务！提高效率，大数据量！

![image-20200723174332606](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723174332606.png)



> 工作窃取算法

工作窃取（work-stealing）算法是指某个线程从其他队列里窃取任务来执行。工作窃取的运行流程图如下：

![image-20200723174400406](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200723174400406.png)

那么为什么需要使用工作窃取算法呢？假如我们需要做一个比较大的任务，我们可以把这个任务分割为若干互不依赖的子任务，为了减少线程间的竞争，于是把这些子任务分别放到不同的队列里，并为每个队列创建一个单独的线程来执行队列里的任务，线程和队列一一对应，比如 A 线程负责处理 A 队列里的任务。但是有的线程会先把自己队列里的任务干完，而其他线程对应的队列里还有任务等待处理。干完活的线程与其等着，不如去帮其他线程干活，于是它就去其他线程的队列里窃取一个任务来执行。而在这时它们会访问同一个队列，所以为了减少窃取任务线程和被窃取任务线程之间的竞争，通常会使用双端队列，被窃取任务线程永远从双端队列的头部拿任务执行，而窃取任务的线程永远从双端队列的尾部拿任务执行。

工作窃取算法的优点是充分利用线程进行并行计算，并减少了线程间的竞争，其缺点是在某些情况下还是存在竞争，比如双端队列里只有一个任务时。并且消耗了更多的系统资源，比如创建多个线程和多个双端队列。

ForkJoinPool

![image-20200724093215119](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200724093215119.png)

![image-20200724093258711](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200724093258711.png)

![image-20200724093313528](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200724093313528.png)



> ForkJoinTask

![image-20200724093348772](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200724093348772.png)

![image-20200724093548426](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200724093548426.png)

有返回值





> 计算求和

```java

import java.util.concurrent.RecursiveTask;

/**
 * 求和计算任务 当超过一定临界就使用forkjoin
 * 如何使用forkJoin
 * 1,。forkJoinPool 通过它来执行
 * 2.计算任务 forkJoinPool.execute(ForkJoinTask task)
 * ForkJoinTask 有两种实现类
 * 1.有返回值
 * 2.无返回值
 * 计算类要继承ForkJoinTask
 */
public class ForkJoinDemo extends RecursiveTask<Long> {

    private Long start;
    private Long end;

    //临界值
    private Long temp = 10000L;

    public ForkJoinDemo(Long start,Long end){

        this.start = start;
        this.end = end;

    }

    public static void main(String[] args) {

    }



    @Override
    protected Long compute() {
        if((end-start)<temp){
            Long sum = 0L;
            for(Long i = start;i<=end;i++){
                sum += i;
            }
            return sum;
        }else{
            Long middle = (start+end)/2;
            ForkJoinDemo task1 = new ForkJoinDemo(start,middle);
            task1.fork();
            ForkJoinDemo task2 = new ForkJoinDemo(middle+1,end);
            task2.fork();
            return (Long)(task1.join()+task2.join());
        }
    }

}
import java.util.stream.LongStream;

public class Test1 {

    public static void main(String[] args) {

//        test1();//5992 500000000500000000

        test2();//4734 500000000500000000
//        test3();//500000000500000000 281
    }

    public static void test1(){

        long start =  System.currentTimeMillis();
        Long sum = 0L;
        for(Long i = 0L;i <= 1000000000L; i ++){

            sum +=i;
        }
        long end = System.currentTimeMillis();

        System.out.println("sum = "+ sum +",时间： " + (end-start));
    }

    public static void test2(){

        long start =  System.currentTimeMillis();

        ForkJoinDemo forkJoinDemo = new ForkJoinDemo(0L,1000000000L);

        Long sum = forkJoinDemo.compute();

        long end = System.currentTimeMillis();

        System.out.println("sum = "+ sum +",时间： " + (end-start));


    }

    public static void test3(){
        long start =  System.currentTimeMillis();

        //reduce前面的参数是累积量意思就是本来就有这些，后面是往上加的
        long sum = LongStream.rangeClosed(0L,1000000000L).parallel().reduce(0L,Long::sum);

        long end = System.currentTimeMillis();

        System.out.println("sum = "+ sum +",时间： " + (end-start));
    }

}

```



## 15异步回调

> Future设计的初衷就是为将来的结果建模

这里感觉接受不了回头补坑

## 15.JMM

Volatile是java虚拟机提供的==轻量级的同步机制==

1.保证可见性

2.==不保证原子性==

3.禁止指令重排

> 什么是JMM

JMM：Java内存模型，不存在的东西，概念！约定！

关于JMM的一些约定：

1.线程解锁前，必须把共享变量==立刻==刷回主存

2.线程加锁前，必须读取主存中的最新值到工作内存中

3.加锁与解锁是同一把锁

![image-20200724111144517](C:\Users\58max\AppData\Roaming\Typora\typora-user-images\image-20200724111144517.png)

8种操作

1. **lock(锁定)：**作用于主内存的变量，一个变量在同一时间只能一个线程锁定。该操作表示该线程独占锁定的变量。
2. **unlock(解锁)：**作用于主内存的变量，表示这个变量的状态由处于锁定状态被释放，这样其他线程才能对该变量进行锁定。
3. **read(读取)：**作用于主内存变量，表示把一个主内存变量的值传输到线程的工作内存，以便随后的load操作使用。
4. **load(载入)：**作用于线程的工作内存的变量，表示把read操作从主内存中读取的变量的值放到工作内存的变量副本中(副本是相对于主内存的变量而言的)。
5. **use(使用)：**作用于线程的工作内存中的变量，表示把工作内存中的一个变量的值传递给执行引擎，每当虚拟机遇到一个需要使用变量的值的字节码指令时就会执行该操作。
6. **assign(赋值)：**作用于线程的工作内存的变量，表示把执行引擎返回的结果赋值给工作内存中的变量，每当虚拟机遇到一个给变量赋值的字节码指令时就会执行该操作。
7. **store(存储)：**作用于线程的工作内存中的变量，把工作内存中的一个变量的值传递给主内存，以便随后的write操作使用。
8. **write(写入)：**作用于主内存的变量，把store操作从工作内存中得到的变量的值放入主内存的变量中。

JMM规定了以上8中操作需要按照如下规则进行

- 不允许read和load、store和write操作之一单独出现，即不允许一个变量从主内存读取了但工作内存不接受，或者从工作内存发起回写了但主内存不接受的情况出现。
- 不允许一个线程丢弃它的最近的assign操作，即变量在工作内存中改变了之后必须把该变化同步回主内存。
- 不允许一个线程无原因地（没有发生过任何assign操作）把数据从线程的工作内存同步回主内存中。
- 一个新的变量只能在主内存中“诞生”，不允许在工作内存中直接使用一个未被初始化（load或assign）的变量，换句话说就是对一个变量实施use和store操作之前，必须先执行过了assign和load操作。
- 一个变量在同一个时刻只允许一条线程对其进行lock操作，但lock操作可以被同一条线程重复执行多次，多次执行lock后，只有执行相同次数的unlock操作，变量才会被解锁。
- 如果对一个变量执行lock操作，将会清空工作内存中此变量的值，在执行引擎使用这个变量前，需要重新执行load或assign操作初始化变量的值。
- 如果一个变量事先没有被lock操作锁定，则不允许对它执行unlock操作，也不允许去unlock一个被其他线程锁定住的变量。



## 16.Volatile

> 1.可见性

```java
import java.util.concurrent.TimeUnit;


/**
 * 我们需要让线程知道num中的值发生了变化
 */
public class Demo {

    /**
     * volatile 保证可见性
     */
    private volatile static int num =0 ;

    public static void main(String[] args) {



        new Thread(()->{
            while (num == 0){

                System.out.println("======================");
            }
        }).start();

        try{
            TimeUnit.SECONDS.sleep(1);
        }catch (Exception e){
            e.printStackTrace();
        }

        num = 1;
        System.out.println(num);
    }
}
```



> 2.不保证原子性

原子性：不可分割

```java
public class Demo02 {

    /**
     * volatile 不保证原子性
     */
    private volatile static int num = 0;

    public  static void add(){
        num++;
    }
    public static void main(String[] args) {

        for(int i = 0;i< 20;i++){
            new Thread(()->{
                for(int j = 0; j< 1000;j++){
                    add();
                }

            }).start();
        }

        while(Thread.activeCount()>2){
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + " " +num);
    }
}
```



如果不加lock锁和关键字synchronized如何保证原子性

```java
import java.util.concurrent.atomic.AtomicInteger;

public class Demo02 {

    /**
     * volatile 不保证原子性
     */
//    private volatile static int num = 0;

    private  volatile static AtomicInteger num1 = new AtomicInteger();

    public  static void add(){
        num1.getAndIncrement();
    }
    public static void main(String[] args) {

        for(int i = 0;i< 20;i++){
            new Thread(()->{
                for(int j = 0; j< 1000;j++){
                    add();
                }

            }).start();
        }

        while(Thread.activeCount()>2){
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + " " +num1);
    }
}
```



> 3.禁止指令重排

什么是指令重排：

源代码-->编译器的优化重排-->指令并行的重排-->内存系统重排-->执行

volatile可以避免指令重排

在该操作上下添加5内存屏障，禁止指令的重排

1.保证特定操作的执行顺序

2.可以保证某些内存变量的可见性

## 17.单例模式

> 饿汉式

```java
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
```

> 懒汉式

```java
/**
 *懒汉式单例
 * 但是在多线程下是有问题的
 * 此时我们可以使用双重检测锁
 * 但是我们能通过反射来破坏单例然后 我们使用三重检测锁
 * 但是我们可以不使用创建单例的方法来创建对象而是使用反射的方法来创建对象，此时我们就可以破坏单例模式
 * 我们还可以使用红绿灯的方式来防止
 * 但是仍旧可以通过反编译来获取红绿灯标志并且通过反射来改变红绿的灯值来破坏单例
 * 所以我们可以通过枚举
 */
public class LazyMan {

    //红绿灯
    private static boolean max = false;

    private LazyMan() {

        synchronized (LazyMan.class) {
            if(  max == false){
                max = true;
            }else{
                throw new RuntimeException("不要破坏单例模式");
            }
            if (Lazyman != null) {
                throw new RuntimeException("不要破坏单例模式");
            }
        }
    }


    private volatile   static LazyMan Lazyman = null;

    /**
     * 双重检测锁模式，懒汉单例模式，DCL懒汉模式
     * @return
     */
    public static  LazyMan getInstance(){

        if (Lazyman == null) {
            synchronized (LazyMan.class) {
                if (Lazyman == null) {
                    Lazyman = new LazyMan();//不是一个原子性操作
                    /**  ,m.
                     * 1.分配内存空间
                     * 2.执行构造方法，初始化对象
                     * 3.把这个对象指向这个空间
                     * 可能会发生指令重排的现象
                     */
                }
            }
        }


        return Lazyman;

    }


}
```

> 静态内部类

```java
/**
 * 静态内部类
 */
public class Holder {

    private Holder(){}

    private static Holder getInstance(){
        return InnerClass.HOLDER;
    }

    public static class InnerClass{

        private static final Holder HOLDER = new Holder();

    }
}
```

> 单例模式不安全使用枚举

无法通过反射来破坏枚举

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum EnumSingle {

    INSTANCE;
    public EnumSingle getInstance(){
        return INSTANCE;
    }

}
class Test{
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<EnumSingle> enumSingleConstructor =  EnumSingle.class.getDeclaredConstructor(null);
        enumSingleConstructor.setAccessible(true);
        EnumSingle instance2 = enumSingleConstructor.newInstance();
    }
}
```



## 18.CAS

> 什么是CAS

> Unsafe

```java
private static final Unsafe unsafe = Unsafe.getUnsafe();
private static final long valueOffset;

static {
    try {
        valueOffset = unsafe.objectFieldOffset
            (AtomicInteger.class.getDeclaredField("value"));
    } catch (Exception ex) { throw new Error(ex); }
}

private volatile int value;
```

> ```
> getAndIncrement()
> ```



```java
  public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
   public final int getAndAddInt(Object o, long offset, int delta) {
        int v;
        do {
            //获取内存地址当中的值
            v = getIntVolatile(o, offset);
            //如果该对象的内存地址中的值和v一样那么就更新这个值
        } while (!compareAndSwapInt(o, offset, v, v + delta));//自旋锁 有可能会阻塞
        return v;
    }
```



总结：CAS 比较当前工作内存的值和主存的值 ，如果一样那么就执行操作

缺点：由于是自旋锁 那么会 消耗时间

 一次性只能保证一个共享变量的原子性

有可能存在ABA问题



> CAS:ABA问题（狸猫换太子）

很好理解



## 19.原子引用

带版本的原子操作，当有现成操作过内存的值是就改变版本号

```java
private static class Pair<T> {
        final T reference;
        final int stamp;
        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }
        static <T> Pair<T> of(T reference, int stamp) {
            return new Pair<T>(reference, stamp);
        }
    }
```

```java
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

```



## 20.各种锁的理解

1.公平锁，非公平锁

2.可重入锁（递归锁）

拿到了外面的锁就可以拿到内部的锁（自动获得）

3.自旋锁

cas就是一个自旋锁

4.死锁

使用jsp定位死锁问题

jsp -l 定位进程号

jstack pid  找到死锁
