package com.example.batch.domain.main;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Main {

    @Id @Column(name = "main_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "desc")
    private String desc;

    @Builder
    public Main(String desc) {
        this.desc = desc;
    }
}
