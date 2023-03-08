package com.rickie_job.mf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rickie_job.mf.mapper")
public class MFApplication {

    public static void main(String[] args) {
        SpringApplication.run(MFApplication.class, args);
    }

}
