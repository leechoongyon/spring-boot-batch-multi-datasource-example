package com.example.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.example.batch.config"})
public class TestConfig {
}

