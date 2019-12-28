package com.mashibing;

import com.mashibing.redis.TestRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class SpringbootStudyApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SpringbootStudyApplication.class, args);
		TestRedis redis = ctx.getBean(TestRedis.class);
		redis.testRedis();

	}

}
