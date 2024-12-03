package com.fastcampus.batchcampus;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

//@Configuration
public class ItemProcessorJobConfiguration {

    // Job 빈 생성: JobRepository와 Step을 사용하여 itemReaderJob 객체 생성
    @Bean
    public Job job(JobRepository jobRepository,
                   Step step) {
        return new JobBuilder("itemReaderJob", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .incrementer(new RunIdIncrementer()) // Job 실행 시마다 고유한 ID를 부여
                .start(step) // Job의 시작 단계로 step 지정
                .build(); // Job 객체 생성 및 반환
    }

    // Step 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 Step 객체 생성
    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ItemReader<User> flatFileItemReader) {

        // ItemProcessor 목록 생성: 여러 개의 프로세서를 조합하여 사용
        final List<ItemProcessor<User, User>> list = Arrays.asList(processor1(), processor2(), processor3());

        return new StepBuilder("step", jobRepository)
                .<User, User>chunk(2, transactionManager) // 청크 단위로 처리: 2개씩 읽고 트랜잭션 관리
                .reader(flatFileItemReader) // ItemReader 설정
                .processor(new CompositeItemProcessor<>(list)) // 여러 프로세서를 조합하여 사용
                .writer(System.out::println) // ItemWriter 설정: 읽은 데이터를 콘솔에 출력
                .build();
    }

    // 사용자 정보를 포맷팅하는 커스텀 프로세서
    private static ItemProcessor<User, String> customProcessor() {
        return user -> {
            if (user.getName().equals("민수")) return null; // 이름이 '민수'인 경우 null 반환
            return "%s의 나이는 %s입니다. 사는 곳은 %s, 전화번호는 %s 입니다.".formatted(
                    user.getName(), user.getAge(), user.getRegion(), user.getTelephone()
            ); // 사용자 정보를 포맷팅하여 문자열로 반환
        };
    }

    // 사용자 이름을 두 배로 늘리는 프로세서
    private static ItemProcessor<User, User> processor1() {
        return user -> {
            user.setName(user.getName() + user.getName()); // 이름을 두 번 반복
            return user; // 변경된 사용자 객체 반환
        };
    }

    // 사용자 나이를 두 배로 늘리는 프로세서
    private static ItemProcessor<User, User> processor2() {
        return user -> {
            user.setAge(user.getAge() + user.getAge()); // 나이를 두 배로 증가
            return user; // 변경된 사용자 객체 반환
        };
    }

    // 사용자 지역을 두 배로 늘리는 프로세서
    private static ItemProcessor<User, User> processor3() {
        return user -> {
            user.setRegion(user.getRegion() + user.getRegion()); // 지역명을 두 번 반복
            return user; // 변경된 사용자 객체 반환
        };
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
}
