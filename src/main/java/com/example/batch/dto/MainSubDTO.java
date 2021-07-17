package com.example.batch.dto;


import com.example.batch.domain.main.Main;
import com.example.batch.domain.sub.Sub;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MainSubDTO {
    private Main main;
    private Sub sub;

    @Builder
    public MainSubDTO(Main main, Sub sub) {
        this.main = main;
        this.sub = sub;
    }
}
