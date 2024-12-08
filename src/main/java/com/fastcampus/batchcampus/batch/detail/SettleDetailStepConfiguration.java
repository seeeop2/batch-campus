package com.fastcampus.batchcampus.batch.detail;

import com.fastcampus.batchcampus.domain.ApiOrder;
import com.fastcampus.batchcampus.domain.SettleDetail;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class SettleDetailStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    // 첫 번째 스텝: 파일의 고객 + 서비스별로 집계를 해서 Execution Context 안에 넣는다.
    // preSettleDetailStep: 고객 및 서비스별 집계 작업을 수행하는 스텝 정의
    @Bean
    public Step preSettleDetailStep(FlatFileItemReader<ApiOrder> preSettleDetailReader,
                                    preSettleDetailWriter preSettleDetailWriter,
                                    ExecutionContextPromotionListener executionContextPromotionListener){
        return new StepBuilder("preSettleDetailStep", jobRepository) // StepBuilder를 사용하여 스텝 이름 및 JobRepository 설정
                .<ApiOrder, Key>chunk(5000, platformTransactionManager) // 청크 크기 설정 및 트랜잭션 관리
                .reader(preSettleDetailReader) // 데이터 읽기 위한 Reader 설정
                .processor(new PreSettleDetailProcessor()) // 데이터 처리를 위한 Processor 설정
                .writer(preSettleDetailWriter) // 데이터 쓰기 위한 Writer 설정
                .listener(executionContextPromotionListener) // ExecutionContextPromotionListener 추가
                .build();
    }

    @StepScope // Job 파라미터를 이용하기 위해 설정
    @Bean
    public FlatFileItemReader<ApiOrder> preSettleDetailReader(
        @Value("#{jobParameters['targetDate']}") String targetDate // Job 파라미터에서 targetDate 주입
    ){
        final String fileName = targetDate + "_api_orders.csv";

        return new FlatFileItemReaderBuilder<ApiOrder>() // FlatFileItemReaderBuilder를 사용하여 리더 설정
                .name("preSettleDetailReader") // 리더 이름 설정
                .resource(new ClassPathResource("/datas/" + fileName)) // 파일 경로 설정
                .linesToSkip(1) // 첫 번째 줄(헤더) 건너뛰기
                .delimited() // 구분자 설정
                .names("id", "customerId", "url", "state", "createdAt") // CSV 파일의 필드 이름 설정
                .targetType(ApiOrder.class) // 읽어들일 타입 설정
                .build();
    }

    // ExecutionContextPromotionListener 설정 : 특정 데이터를 다른 스텝으로 전달하기 위해 필요한 리스너
    @Bean
    public ExecutionContextPromotionListener promotionListener(){
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener(); // 리스너 인스턴스 생성
        listener.setKeys(new String[]{"snapshots"}); // ExecutionContext에 프로모션할 키 설정
        return listener; // 리스너 반환
    }

    // 두 번째 스텝: 집계된 Execution Context 데이터를 가지고 DB에 Write 한다.
    @Bean
    public Step settleDetailStep(SettleDetailReader settleDetailReader,
                                 SettleDetailProcessor settleDetailProcessor,
                                 JpaItemWriter<SettleDetail> settleDetailWriter){
        return new StepBuilder("settleDetailStep", jobRepository)
                .<KeyAndCount, SettleDetail>chunk(1000, platformTransactionManager)
                .reader(settleDetailReader)
                .processor(settleDetailProcessor)
                .writer(settleDetailWriter)
                .build();
    }
    @Bean
    public JpaItemWriter<SettleDetail> settleDetailWriter(EntityManagerFactory entityManagerFactory){
        return new JpaItemWriterBuilder<SettleDetail>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
