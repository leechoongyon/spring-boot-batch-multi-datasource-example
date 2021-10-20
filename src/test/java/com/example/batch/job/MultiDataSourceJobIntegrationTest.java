package com.example.batch.job;


import com.example.batch.TestConfig;
import com.example.batch.bean.MultiDataSourceBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes={MultiDataSourceJob.class, MultiDataSourceBean.class, TestConfig.class})
public class MultiDataSourceJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    /**
     * 테스트를 위해 MYSQL 과 H2 서버 띄워야 함
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void 멀티데이터소스_통합_테스트() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    }
}
