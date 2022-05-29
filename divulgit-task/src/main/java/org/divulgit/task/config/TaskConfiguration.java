package org.divulgit.task.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class TaskConfiguration implements AsyncConfigurer {

    @Value("${tasks.thread-executor.core-pool-size:2}")
    private int corePoolSize;

    @Value("${tasks.thread-executor.max-pool-size:10}")
    private int maxPoolSize;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix("ScanExecutor-");
        executor.initialize();
        return executor;
    }
}
