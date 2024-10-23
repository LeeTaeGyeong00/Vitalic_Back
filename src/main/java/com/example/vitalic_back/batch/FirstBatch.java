package com.example.vitalic_back.batch;

import com.example.vitalic_back.entity.EnterPassbook;
import com.example.vitalic_back.entity.Passbook;
import com.example.vitalic_back.repository.EnterPassbookRepository;
import com.example.vitalic_back.repository.PassbookRepository;
import com.example.vitalic_back.service.MailService;
import com.example.vitalic_back.service.UserService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final UserService userService;
    private final EnterPassbookRepository beforeRepository;
    private final PassbookRepository afterRepository;
    private final MailService mailService; // MailService 주입
    public FirstBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, UserService userService, EnterPassbookRepository beforeRepository, PassbookRepository afterRepository, MailService mailService) {

        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.userService = userService;
        this.beforeRepository = beforeRepository;
        this.afterRepository = afterRepository;
        this.mailService = mailService;
    }

    @Bean
    public Job firstJob() {

        System.out.println("first job");

        return new JobBuilder("firstJob", jobRepository)
                .start(firstStep())
                .next(compareBudgetStep())
                .next(deleteStep()) // 데이터를 처리한 후 삭제하는 스텝 추가
                .build();
    }

    @Bean
    public Step firstStep() {

        System.out.println("first step");

        return new StepBuilder("firstStep", jobRepository)
                .<EnterPassbook, Passbook> chunk(10, platformTransactionManager)
                .reader(beforeReader())
                .processor(middleProcessor())
                .writer(afterWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<EnterPassbook> beforeReader() {

        return new RepositoryItemReaderBuilder<EnterPassbook>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<EnterPassbook, Passbook> middleProcessor() {

        return new ItemProcessor<EnterPassbook, Passbook>() {

            @Override
            public Passbook process(EnterPassbook item) throws Exception {

                Passbook afterEntity = new Passbook();
                afterEntity.setBank_name(item.getBank_name());        // 은행 기관 명
                afterEntity.setAccount_number(item.getAccount_number()); // 계좌번호
                afterEntity.setBalance_amt(item.getBalance_amt());     // 계좌 잔액
                afterEntity.setInout_type(item.getInout_type());       // 입출금 구분
                afterEntity.setIn_des(item.getIn_des());                // 입금처
                afterEntity.setOut_des(item.getOut_des());              // 출금처
                afterEntity.setOut_type(item.getOut_type());            // 출금처 카테고리
                afterEntity.setTran_date_time(item.getTran_date_time()); // 거래 일자
                afterEntity.setTran_type(item.getTran_type());          // 거래 구분
                afterEntity.setTran_amt(item.getTran_amt());            // 거래 금액
                afterEntity.setAfter_balance_amt(item.getAfter_balance_amt()); // 거래 후 잔액

                return afterEntity;
            }
        };
    }

    // UserService를 이용한 소비량 비교 로직을 처리하는 Step
    // 소비량 비교 후 이메일 발송 로직을 포함하는 Step
    @Bean
    public Step compareBudgetStep() {
        return new StepBuilder("compareBudgetStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long userId = 1L; // 예시 사용자 ID
                    userService.compareUserBudgetWithDjango(userId);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
    // 데이터 삭제를 위한 Step 추가
    @Bean
    public Step deleteStep() {
        return new StepBuilder("deleteStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    beforeRepository.deleteAll(); // 처리된 데이터 삭제
                    System.out.println("All EnterPassbook records deleted");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
    @Bean
    public RepositoryItemWriter<Passbook> afterWriter() {

        return new RepositoryItemWriterBuilder<Passbook>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}