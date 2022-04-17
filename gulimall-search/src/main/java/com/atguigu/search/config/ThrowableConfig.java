package com.atguigu.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThrowableConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 10,
                                 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5, true),
                                 Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        return threadPoolExecutor;
    }
}
