package com.fastcampus.batchcampus;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ItemReaderJobConfiguration {

    // Job 빈 생성: JobRepository와 Step을 사용하여 itemReaderJob 객체 생성
    @Bean
    public Job job (JobRepository jobRepository,
                    Step step){
        return new JobBuilder("itemReaderJob", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .incrementer(new RunIdIncrementer()) // Job 실행 시마다 고유한 ID를 부여
                .start(step) // Job의 시작 단계로 step 지정
                .build(); // Job 객체 생성 및 반환
    }

    // Step 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 Step 객체 생성
    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ItemReader<User> jsonItemReader){
        return new StepBuilder("step", jobRepository)
                .<User, User>chunk(2, transactionManager) // 청크 단위로 처리: 2개씩 읽고 트랜잭션 관리
                .reader(jsonItemReader) // ItemReader 설정: JSON 파일로부터 읽기
                .writer(System.out::println) // ItemWriter 설정: 읽은 데이터를 콘솔에 출력
                .build();
    }

    // FlatFileItemReader 빈 생성: CSV 파일을 읽어오는 Reader 생성
    @Bean
    public FlatFileItemReader<User> flatFileItemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("flatFileItemReader") // Reader 이름 설정
                .resource(new ClassPathResource("users.txt")) // 읽어올 파일 경로 설정
                .linesToSkip(2) // 처음 2줄은 건너뜀
                .delimited().delimiter(",") // 구분자 설정: 쉼표
                .names("name", "age", "region", "telephone") // 파일에서 읽어올 필드 이름 설정
                .targetType(User.class) // 읽은 데이터를 User 객체로 변환
                .strict(true) // true: 파일 없으면 에러, false: 파일 없으면 넘어감
                .build();
    }

    // FixedLengthItemReader 빈 생성: 고정 길이의 파일을 읽어오는 Reader 생성
    @Bean
    public FlatFileItemReader<User> fixedLengthFlatFileItemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("fixedLengthFlatFileItemReader") // Reader 이름 설정
                .resource(new ClassPathResource("usersFixedLength.txt")) // 읽어올 파일 경로 설정
                .linesToSkip(2) // 처음 2줄은 건너뜀
                .fixedLength() // 고정 길이 방식으로 읽기 설정
                .columns(new Range[]{new Range(1,2), new Range(3,4), new Range(5,6), new Range(7,19)}) // 각 필드의 길이 설정
                .names("name", "age", "region", "telephone") // 파일에서 읽어올 필드 이름 설정
                .targetType(User.class) // 읽은 데이터를 User 객체로 변환
                .strict(true) // true: 파일 없으면 에러, false: 파일 없으면 넘어감
                .build();
    }

    // JsonItemReader 빈 생성: JSON 파일을 읽어오는 Reader 생성
    @Bean
    public JsonItemReader<User> jsonItemReader(){
        return new JsonItemReaderBuilder<User>()
                .name("jsonItemReader") // Reader 이름 설정
                .resource(new ClassPathResource("users.json")) // 읽어올 파일 경로 설정
                .jsonObjectReader(new JacksonJsonObjectReader<>(User.class)) // JSON 객체를 User 클래스에 맞게 읽기 설정
                .build();
    }

}
