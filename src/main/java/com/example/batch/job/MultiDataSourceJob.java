package com.example.batch.job;

import com.example.batch.bean.MultiDataSourceBean;
import com.example.batch.config.JpaConfiguration;
import com.example.batch.dto.MainSubDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MultiDataSourceJob {
    private static final int CHUNK_SIZE = 1;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager chainedTransactionManager;

    private final MultiDataSourceBean multiDataSourceBean;

    public MultiDataSourceJob(JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory,
                              @Qualifier(JpaConfiguration.CHAINED_TRANSACTION_MANAGER) PlatformTransactionManager chainedTransactionManager,
                              MultiDataSourceBean multiDataSourceBean) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.chainedTransactionManager = chainedTransactionManager;
        this.multiDataSourceBean = multiDataSourceBean;
    }

    @Bean
    public Job multiDataSourceJob01() {
        return jobBuilderFactory.get("multiDataSourceJob01")
                .start(multiDataSourceStep01())
                .build()
                ;
    }

    @Bean
    public Step multiDataSourceStep01() {
        return stepBuilderFactory.get("multiDataSourceStep01")
                .<MainSubDTO, MainSubDTO>chunk(CHUNK_SIZE)
                .reader(multiDataSourceBean)
                .writer(multiDataSourceBean)
                .transactionManager(chainedTransactionManager)
                .build()
                ;
    }
}