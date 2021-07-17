package com.example.batch.domain.sub;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sub {

    @Id
    @Column(name = "sub_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "etc")
    private String etc;

    @Builder
    public Sub(String etc) {
        this.etc = etc;
    }
}
