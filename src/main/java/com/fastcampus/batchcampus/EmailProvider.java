package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;

// 이메일 전송 기능을 정의하는 인터페이스
public interface EmailProvider {

    // 이메일 전송 메서드
    void send(String emailAddress, String title, String body);

    // Fake 구현체: 실제 이메일 전송 대신 로그로 출력하는 클래스
    @Slf4j
    class Fake implements EmailProvider {

        @Override
        public void send(String emailAddress, String title, String body) {
            // 이메일 전송 완료 로그 출력
            log.info("{} email 전송 완료! {} : {}", emailAddress, title, body);
        }
    }
}
