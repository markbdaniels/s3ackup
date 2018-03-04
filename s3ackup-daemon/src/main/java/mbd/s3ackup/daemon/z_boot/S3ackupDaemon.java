package mbd.s3ackup.daemon.z_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan(basePackages = { "mbd.s3ackup.daemon" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class S3ackupDaemon {

	public static void main(String[] args) {
		SpringApplication.run(S3ackupDaemon.class, args);
	}

}
