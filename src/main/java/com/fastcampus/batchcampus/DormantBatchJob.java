package com.fastcampus.batchcampus;

import com.fastcampus.batchcampus.batch.BatchStatus;
import com.fastcampus.batchcampus.batch.JobExecution;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DormantBatchJob {

    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    // 생성자: CustomerRepository 주입 및 Fake EmailProvider 초기화
    public DormantBatchJob(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailProvider = new EmailProvider.Fake();
    }

    // 배치 작업 실행 메서드
    public JobExecution execute(){

        final JobExecution jobExecution = new JobExecution();
        // 배치 작업 시작 상태 설정
        jobExecution.setStatus(BatchStatus.STARTING);
        // 시작 시간 기록
        jobExecution.setStartTime(LocalDateTime.now());

        // 페이지 번호 초기화
        int pageNo = 0;

        try {
            while(true){
                // 1. 유저를 조회한다.
                final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending());
                final Page<Customer> page = customerRepository.findAll(pageRequest);

                final Customer customer;

                // 페이지가 비어있다면, 배치 작업이 끝난 것으로 판단하고 중단함.
                if (page.isEmpty()){
                    break;
                // 페이지가 있다면, 페이지 번호를 올리고, 페이지 내 존재하는 내용을 Customer 객체에 담음.
                } else{
                    pageNo ++;
                    customer = page.getContent().get(0);
                }

                // 2. 휴면계정 대상을 추출 및 변환한다.
                final boolean isDormantTarget = LocalDate.now()
                        // 현재 날짜에서 365일 전
                        .minusDays(365)
                        // 마지막 로그인 날짜와 비교
                        .isAfter(customer.getLoginAt().toLocalDate());

                if (isDormantTarget){
                    // 휴면 상태로 변경
                    customer.setStatus(Customer.Status.DORMANT);
                } else{
                    // 휴면 대상이 아니면 다음 고객으로 넘어감
                    continue;
                }

                // 3. 휴면계정으로 상태를 변경한다.
                // 변경된 고객 정보 저장
                customerRepository.save(customer);

                // 4. 메일을 보낸다.
                emailProvider.send(customer.getEmail(), "휴면 전환 안내 메일입니다.", "내용");
            }

            // 배치 작업 완료 상태 설정
            jobExecution.setStatus(BatchStatus.COMPLETED);

        } catch (Exception e){
            // 예외 발생 시 실패 상태 설정
            jobExecution.setStatus(BatchStatus.FAILED);
        }

        // 종료 시간 기록
        jobExecution.setEndTime(LocalDateTime.now());

        // 배치 작업 완료 후 관리자에게 알림 메일 전송
        emailProvider.send("admin@fastcampus.com", "배치 완료 알림", "DormantBatchJob이 수행되었습니다. status: " + jobExecution.getStatus());

        return jobExecution;
    }

}
