package com.spring.mybox_mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MyboxMysqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyboxMysqlApplication.class, args);
    }

}
