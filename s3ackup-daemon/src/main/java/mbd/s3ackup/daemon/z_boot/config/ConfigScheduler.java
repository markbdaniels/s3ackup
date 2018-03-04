package mbd.s3ackup.daemon.z_boot.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class ConfigScheduler implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());

	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(3);
	}

	@Bean(destroyMethod = "shutdown")
	public Executor cloudActionExecutor() {
		int threadCount = 5;
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, threadCount, 1L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(threadCount));
		threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		return threadPoolExecutor;
	}
}
