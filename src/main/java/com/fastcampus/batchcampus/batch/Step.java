package com.fastcampus.batchcampus.batch;

import com.fastcampus.batchcampus.customer.Customer;
import lombok.Builder;

// 배치 작업의 단계를 정의하는 Step 클래스
public class Step {
    private final Tasklet tasklet; // 실행할 작업을 나타내는 Tasklet

    public Step(Tasklet tasklet) {
        this.tasklet = tasklet;
    }

    @Builder
    public Step(ItemReader<Customer> itemReader,
                ItemProcessor<Customer,Customer> itemProcessor,
                ItemWriter<Customer> itemWriter) {
        this.tasklet = new SimpleTasklet(itemReader, itemProcessor, itemWriter);
    }

    // Tasklet을 실행하는 메서드
    public void execute() {
        tasklet.execute(); // Tasklet의 execute() 메서드 호출
    }
}
