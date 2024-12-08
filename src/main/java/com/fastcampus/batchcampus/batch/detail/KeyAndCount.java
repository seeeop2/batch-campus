package com.fastcampus.batchcampus.batch.detail;

// 거래를 식별하는 Key 객체와 해당 거래 수량(count)을 포함하는 불변 레코드 클래스
// 레코드 클래스는 자동으로 생성된 생성자, equals(), hashCode(), toString() 메서드를 포함

// KeyAndCount 클래스는 Key 객체와 거래 수량을 함께 저장하여 처리하기 위한 구조체 역할
public record KeyAndCount(Key key, Long count) {
}
