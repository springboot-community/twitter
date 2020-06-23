package io.springboot.twitter.configuration;



import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class ThreadPoolTaskExecutorConfiguration {
	
	@Bean(initMethod = "initialize", destroyMethod = "destroy")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		
		// TODO 针对系统和场景的优化参数
		
		threadPoolTaskExecutor.setThreadNamePrefix("task-executor-");
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		
		return threadPoolTaskExecutor;
	}
}
