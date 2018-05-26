package com.tachnostack.configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfiguration {

	@Value("${threadpool.corepoolsize}")
    int corePoolSize;
     
    @Value("${threadpool.maxpoolsize}")
    int maxPoolSize;
     
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setQueueCapacity(500);
        pool.setThreadNamePrefix("Thread Lookup-");
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.initialize();
        return pool;
    }
    
    @Bean
    public ConcurrentMap<Long, Long> getHashMap(){
    	return new ConcurrentHashMap<Long, Long>();
    }
}
