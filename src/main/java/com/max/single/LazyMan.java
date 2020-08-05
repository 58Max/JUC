package com.max.single;


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
                    /**
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
