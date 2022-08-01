package com.make;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.make.dao")
@EnableScheduling
public class Springboot2022nocvApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot2022nocvApplication.class, args);
    }

}
