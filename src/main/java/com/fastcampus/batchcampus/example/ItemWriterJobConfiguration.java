package com.fastcampus.batchcampus.example;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

//@Configuration
public class ItemWriterJobConfiguration {

    // Job 빈 생성: JobRepository와 Step을 사용하여 itemReaderJob 객체 생성
    @Bean
    public Job job (JobRepository jobRepository,
                    Step step){
        return new JobBuilder("itemReaderJob", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .incrementer(new RunIdIncrementer()) // Job 실행 시마다 고유한 ID를 부여
                .start(step) // Job의 시작 단계로 step 지정
                .build(); // Job 객체 생성 및 반환
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ItemReader<User> flatFileItemReader,
                     ItemWriter<User> jdbcBatchItemWriter){
        return new StepBuilder("step", jobRepository)
                .<User, User>chunk(2, transactionManager) // 청크 단위로 처리: 2개씩 읽고 트랜잭션 관리
                .reader(flatFileItemReader) // ItemReader 설정: CSV 파일로부터 읽기
                .writer(jdbcBatchItemWriter) // ItemWriter 설정
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

    // FlatFileItemWriter 빈 생성: CSV 파일에 데이터를 쓰는 Writer 생성
    @Bean
    public ItemWriter<User> flatFileItemWriter(){
        return new FlatFileItemWriterBuilder<User>()
                .name("flatFileItemWriter") // Writer 이름 설정
                .resource(new PathResource("src/main/resources/new_users.txt")) // 쓰기 파일 경로 설정
                .delimited().delimiter("__") // 구분자 설정: 이중 언더스코어
                .names("name", "age", "region", "telephone") // 작성할 필드 이름 설정
                .build();
    }

    // 포맷팅된 FlatFileItemWriter 빈 생성: 포맷에 맞춰 데이터를 쓰는 Writer 생성
    @Bean
    public ItemWriter<User> formattedFlatFileItemWriter(){
        return new FlatFileItemWriterBuilder<User>()
                .name("formattedFlatFileItemWriter") // Writer 이름 설정
                .resource(new PathResource("src/main/resources/new_formatted_users.txt")) // 쓰기 파일 경로 설정
                .formatted() // 포맷팅된 출력 설정
                .format("%s의 나이는 %s입니다. 사는 곳은 %s, 전화번호는 %s입니다.") // 출력 포맷 설정
                .names("name", "age", "region", "telephone") // 작성할 필드 이름 설정
//                .shouldDeleteIfExists(false) // 파일이 이미 존재할 경우 삭제 여부 설정: false면 기존 파일을 덮어쓰지 않음
//                .append(true) // 기존 파일에 추가 여부 설정: true면 기존 파일의 끝에 데이터를 추가
                .build();
    }

    // JsonFileItemWriter 빈 생성: JSON 파일에 데이터를 쓰는 Writer 생성
    @Bean
    public JsonFileItemWriter<User> jsonFileItemWriter(){
        return new JsonFileItemWriterBuilder<User>()
                .name("jsonFileItemWriter") // Writer 이름 설정
                .resource(new PathResource("src/main/resources/new_users.json")) // 쓰기 파일 경로 설정
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>()) // JSON 객체를 User 클래스에 맞게 변환
                .build();
    }

    // JpaItemWriter 빈 생성: JPA를 사용하여 데이터베이스에 데이터를 쓰는 Writer 생성
    @Bean
    public ItemWriter<User> jpaItemWriter(EntityManagerFactory entityManagerFactory){
        return new JpaItemWriterBuilder<User>()
                .entityManagerFactory(entityManagerFactory) // JPA EntityManager 설정
                .build();
    }

    // JdbcBatchItemWriter 빈 생성: JDBC를 사용하여 데이터베이스에 데이터를批量 쓰는 Writer 생성
    @Bean
    public ItemWriter<User> jdbcBatchItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource) // 데이터 소스 설정
                .sql("""
                        INSERT INTO
                            USER(name, age, region, telephone)
                        VALUES
                            (:name, :age, :region, :telephone)
                        """) // SQL 쿼리 설정
                .beanMapped() // 객체 필드를 SQL 쿼리 매핑
                .build();
    }

}
