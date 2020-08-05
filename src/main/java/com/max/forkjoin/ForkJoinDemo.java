package com.max.forkjoin;

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
