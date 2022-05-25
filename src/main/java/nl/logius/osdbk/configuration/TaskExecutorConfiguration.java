package nl.logius.osdbk.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.task-executor")
@Data
public class TaskExecutorConfiguration {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueSize;
}
