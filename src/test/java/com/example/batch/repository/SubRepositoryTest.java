package com.example.batch.repository;

import com.example.batch.domain.sub.Sub;
import com.example.batch.repository.sub.SubRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubRepositoryTest {

    @Autowired
    private SubRepository subRepository;

    /**
     * 테스트를 위해 MYSQL 과 H2 서버 띄워야 함
     * @throws Exception
     */
    @Test
    @Transactional
    public void save_테스트() throws Exception {
        Sub sub = Sub.builder()
                .etc("sub etc")
                .build()
                ;
        subRepository.saveAndFlush(sub);
    }

}