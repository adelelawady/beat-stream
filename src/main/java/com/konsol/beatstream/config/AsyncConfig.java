package com.konsol.beatstream.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//@Configuration
public class AsyncConfig {

    @Bean(name = "downloadTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Number of core threads
        executor.setMaxPoolSize(50); // Maximum number of threads in the pool
        executor.setQueueCapacity(100); // Queue capacity
        executor.setThreadNamePrefix("Download-");
        executor.initialize();
        return executor;
    }
}
