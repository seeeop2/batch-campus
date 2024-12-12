package com.fastcampus.batchcampus.batch.group;

import com.fastcampus.batchcampus.domain.Customer;
import com.fastcampus.batchcampus.domain.SettleGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

// SettleGroupStepConfiguration 클래스: 배치 작업의 Step 구성 클래스

@RequiredArgsConstructor
@Configuration
public class SettleGroupStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    // settleGroupStep 메서드: 배치 Step을 구성하는 Bean
    @Bean
    public Step settleGroupStep(SettleGroupReader settleGroupReader,
                                SettleGroupProcessor settleGroupProcessor,
                                ItemWriter<List<SettleGroup>> settleGroupItemWriter) {
        // StepBuilder를 사용하여 Step 구성
        return new StepBuilder("settleGroupStep", jobRepository) // Step 이름과 JobRepository 설정
                .<Customer, List<SettleGroup>>chunk(100, platformTransactionManager) // Chunk 크기와 트랜잭션 매니저 설정
                .reader(settleGroupReader) // ItemReader 설정
                .processor(settleGroupProcessor) // ItemProcessor 설정
                .writer(settleGroupItemWriter) // ItemWriter 설정
                .build();
    }

    // settleGroupItemWriter 메서드: CompositeItemWriter를 구성하는 Bean
    @Bean
    public ItemWriter<List<SettleGroup>> settleGroupItemWriter(SettleGroupItemDBWriter settleGroupItemDBWriter,
                                                               SettleGroupItemMailWriter settleGroupItemMailWriter) {
        // 여러 ItemWriter를 조합하여 CompositeItemWriter 생성
        return new CompositeItemWriter<>(
                settleGroupItemDBWriter, // 데이터베이스에 저장하는 ItemWriter
                settleGroupItemMailWriter // 이메일로 발송하는 ItemWriter
        );
    }
}
