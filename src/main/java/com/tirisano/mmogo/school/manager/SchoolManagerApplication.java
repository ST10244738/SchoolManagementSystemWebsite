package com.tirisano.mmogo.school.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchoolManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolManagerApplication.class, args);
    }
}