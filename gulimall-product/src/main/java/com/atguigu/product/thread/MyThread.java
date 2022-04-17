package com.atguigu.product.thread;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class MyThread {

    public static void main(String[] args) {
        System.err.println(test());
    }
    public static Integer test(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 10,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5, true),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        CompletableFuture.supplyAsync(new Supplier<Integer>(){
            @Override
            public Integer get() {
                for (int i=0;i<100;i++){
                    System.err.print(i+" ");
                }
                return 10;
            }
        },threadPoolExecutor);
        return 1;
    }
}
