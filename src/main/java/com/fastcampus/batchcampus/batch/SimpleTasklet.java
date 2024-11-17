package com.fastcampus.batchcampus.batch;

import com.fastcampus.batchcampus.customer.Customer;
import org.springframework.stereotype.Component;

// 비즈니스 로직 담당

@Component
public class SimpleTasklet implements Tasklet {

    private final ItemReader<Customer> itemReader; // 고객 정보를 읽기 위한 리더
    private final ItemProcessor<Customer, Customer> itemProcessor; // 고객 정보를 처리하기 위한 프로세서
    private final ItemWriter<Customer> itemWriter; // 고객 정보를 저장하기 위한 라이터

    public SimpleTasklet(ItemReader<Customer> itemReader, ItemProcessor<Customer, Customer> itemProcessor, ItemWriter<Customer> itemWriter) {
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
    }

    @Override
    public void execute() {
        // 무한 루프를 통해 고객 정보를 읽고, 처리하고, 저장
        while (true) {
            // READ: 고객 정보를 읽음
            final Customer read = itemReader.read();
            if (read == null) break; // 더 이상 읽을 고객이 없으면 루프 종료

            // PROCESS: 읽은 고객 정보를 처리
            final Customer process = itemProcessor.process(read);
            if (process == null) continue; // 처리 결과가 null이면 다음 고객으로 이동

            // WRITE: 처리된 고객 정보를 저장
            itemWriter.write(process);
        }
    }
}
