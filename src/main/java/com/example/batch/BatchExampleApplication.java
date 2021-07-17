package com.example.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class BatchExampleApplication {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(BatchExampleApplication.class, args)));
    }
}
