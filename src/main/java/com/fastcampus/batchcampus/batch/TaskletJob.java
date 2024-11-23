package com.fastcampus.batchcampus.batch;

import com.fastcampus.batchcampus.customer.Customer;
import lombok.Builder;

// 배치 작업을 실행하는 Job 클래스
public class TaskletJob extends AbstractJob {

    private final Tasklet tasklet; // 실행할 작업

    // Tasklet만을 받는 생성자
    public TaskletJob(Tasklet tasklet) {
        super(null); // JobExecutionListener를 null로 설정하여 기본 리스너 사용
        this.tasklet = tasklet; // 전달받은 Tasklet 객체 저장
    }

    // Builder 패턴을 사용하는 생성자: ItemReader, ItemProcessor, ItemWriter를 받아 SimpleTasklet 생성
    @Builder
    public TaskletJob(ItemReader<Customer> itemReader, ItemProcessor<Customer,Customer> itemProcessor, ItemWriter<Customer> itemWriter, JobExecutionListener jobExecutionListener) {
        super(jobExecutionListener); // JobExecutionListener를 부모 클래스에 전달
        this.tasklet = new SimpleTasklet(itemReader, itemProcessor, itemWriter); // SimpleTasklet 객체 생성 및 초기화
    }

    // 배치 작업의 실행 로직을 정의하는 메서드
    @Override
    public void doExecute() {
        tasklet.execute(); // Tasklet의 execute 메서드 호출
    }
}
