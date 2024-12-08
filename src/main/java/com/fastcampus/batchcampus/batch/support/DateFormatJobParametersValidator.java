package com.fastcampus.batchcampus.batch.support;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateFormatJobParametersValidator implements JobParametersValidator {

    // 날짜 형식을 지정하기 위한 DateTimeFormatter
    private final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final String[] names; // 검증할 파라미터 이름 배열

    // 생성자: 검증할 파라미터 이름을 받음
    public DateFormatJobParametersValidator(String[] name) {
        this.names = name; // 파라미터 이름 배열 초기화
    }

    // JobParameters를 검증하는 메서드
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        // 각 파라미터 이름에 대해 날짜 형식 검증 수행
        for (String name : names) {
            validateDateFormat(parameters, name); // 날짜 형식 검증 메서드 호출
        }
    }

    // 주어진 파라미터의 날짜 형식을 검증하는 메서드
    private void validateDateFormat(JobParameters parameters, String name) throws JobParametersInvalidException {
        try {
            final String string = parameters.getString(name); // 파라미터에서 문자열 값을 가져옴
            LocalDate.parse(Objects.requireNonNull(string), datetimeFormatter); // null 아닐 경우, 날짜 형식으로 파싱
        } catch (Exception e) {
            // 예외 발생 시 JobParametersInvalidException 던짐
            throw new JobParametersInvalidException("yyyyMMdd 형식만을 지원합니다.");
        }
    }
}
