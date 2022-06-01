package nl.logius.osdbk.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfiguration {

    @Value("${spring.task-executor.corePoolSize}")
    private int corePoolSize;

    @Value("${spring.task-executor.maxPoolSize}")
    private int maxPoolSize;

    @Value("${spring.task-executor.queueSize}")
    private int queueSize;

    @Bean
    public Executor executor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix("throttling-service-");
        executor.initialize();
        return executor;
    }

}
