package com.example.batch.bean;


import com.example.batch.domain.main.Main;
import com.example.batch.domain.sub.Sub;
import com.example.batch.dto.MainSubDTO;
import com.example.batch.repository.main.MainRepository;
import com.example.batch.repository.sub.SubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class MultiDataSourceBean implements ItemReader<MainSubDTO>, ItemWriter<MainSubDTO>  {

    private final MainRepository mainRepository;
    private final SubRepository subRepository;

    private int readCount = 10;
    private int currentCount = 0;

    private int writeCount = 0;
    private int stopWriteCount = 5;

    @Override
    public MainSubDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (currentCount == readCount) {
            return null;
        }
        currentCount++;

        Main main = Main.builder()
                .desc("main desc...")
                .build();
        Sub sub = Sub.builder()
                .etc("sub etc...")
                .build();
        return MainSubDTO.builder()
                .main(main)
                .sub(sub)
                .build();
    }

    @Override
    public void write(List<? extends MainSubDTO> items) throws Exception {
        if (writeCount == stopWriteCount) {
            throw new RuntimeException("트랜잭션 테스트");
        }

        items.forEach(item -> {
            mainRepository.save(item.getMain());
            subRepository.save(item.getSub());
        });

        writeCount++;
    }
}
