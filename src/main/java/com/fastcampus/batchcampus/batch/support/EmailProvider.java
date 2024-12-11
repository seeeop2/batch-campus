package com.fastcampus.batchcampus.batch.support;

import lombok.extern.slf4j.Slf4j;

// EmailProvider 인터페이스: 이메일 전송 기능을 정의
public interface EmailProvider {

    // 이메일 전송 메서드: 받는 사람의 이메일 주소, 제목, 본문을 인자로 받음
    void send(String emailAddress, String title, String body);

    // Fake 클래스: 테스트 및 개발 시 사용할 수 있는 EmailProvider의 가짜 구현체
    @Slf4j
    class Fake implements EmailProvider {

        // 이메일 전송 메서드 구현: 실제 전송 대신 로그에 전송 완료 메시지 출력
        @Override
        public void send(String emailAddress, String title, String body) {
            log.info("{} email 전송 완료! \n{} \n{}", emailAddress, title, body); // 이메일 전송 정보를 로그에 기록
        }
    }
}
