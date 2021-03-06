package org.distributed.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("org.distributed.test.mapper")
@ServletComponentScan
public class DrawApplication {
	public static void main(String[] args) {
		SpringApplication.run(DrawApplication.class, args);
	}
}
