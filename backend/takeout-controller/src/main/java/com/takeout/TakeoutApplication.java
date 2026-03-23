package com.takeout;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 外卖平台启动类
 * 
 * @author 小好
 */
@SpringBootApplication
@MapperScan("com.takeout.mapper")
@ComponentScan(basePackages = "com.takeout")
@Slf4j
public class TakeoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakeoutApplication.class, args);
        log.info("========================================");
        log.info("    外卖平台启动成功！🎉");
        log.info("    API 文档：http://localhost:8080/doc.html");
        log.info("========================================");
    }
}
