package com.fastcampus.batchcampus.batch.detail;

import java.io.Serializable;

// Key 레코드 클래스: 고객 ID와 서비스 ID를 포함하는 불변 객체
// 레코드 클래스는 기본적으로 final 필드로 구성되며, 자동으로 생성자, equals(), hashCode(), toString() 메서드가 생성
record Key(Long customerId, Long serviceId) implements Serializable {
}
