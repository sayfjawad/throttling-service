package nl.logius.osdbk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfiguration {

    @Bean
    public Executor executor(TaskExecutorConfiguration taskExecutorConfiguration) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutorConfiguration.getCorePoolSize());
        executor.setMaxPoolSize(taskExecutorConfiguration.getMaxPoolSize());
        executor.setQueueCapacity(taskExecutorConfiguration.getQueueSize());
        executor.setThreadNamePrefix("throttling-service-");
        executor.initialize();
        return executor;
    }
}
