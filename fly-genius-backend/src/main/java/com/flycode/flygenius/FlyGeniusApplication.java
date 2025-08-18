package com.flycode.flygenius;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.flycode.flygenius.mapper")
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
public class FlyGeniusApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlyGeniusApplication.class, args);
    }

}
