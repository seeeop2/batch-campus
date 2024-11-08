package com.fastcampus.batchcampus.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime loginAt;

    private Status status;

    // 생성자: 이름과 이메일을 받아 초기화
    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.loginAt = LocalDateTime.now();
        this.status = Status.NORMAL;
    }

    // Test 하기 위하여, 로그인 시간에 대해서만 Setter 작성함.
    public void setLoginAt(LocalDateTime loginAt) {
        this.loginAt = loginAt;
    }

    // 고객 상태를 정의하는 열거형
    public enum Status {
        NORMAL,
        DORMANT,
        ;
    }

}
