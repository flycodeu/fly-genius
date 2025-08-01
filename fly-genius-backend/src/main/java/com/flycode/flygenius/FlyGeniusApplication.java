package com.flycode.flygenius;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.flycode.flygenius.mapper")
public class FlyGeniusApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlyGeniusApplication.class, args);
    }

}
