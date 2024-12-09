package com.fastcampus.batchcampus.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Customer {

    private Long id; // 고객 ID
    private String name; // 고객 이름
    private String email; // 고객 이메일
    private LocalDateTime createdAt; // 고객 생성 날짜 및 시간
    private LocalDateTime updatedAt; // 고객 정보 수정 날짜 및 시간

    public Customer(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
