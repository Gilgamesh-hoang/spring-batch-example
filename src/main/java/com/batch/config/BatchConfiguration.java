package com.batch.config;

import com.batch.listener.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    private JobRepository jobRepository;

    //    @Bean
//    public Job sampleJob1(JobCompletionNotificationListener listener,
//                          Step step1,
//                          Step step2
//    ) {
//        return new JobBuilder("sampleJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .start(step1)
//                .next(step2)
//                .listener(listener)
//                .build();
//    }
    @Bean
    public Job orderProcessingJob(JobCompletionNotificationListener listener,
                                  Step importOrderStep,
                                  Step exportOrderSummaryStep) {
        return new JobBuilder("orderProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importOrderStep)
                .next(exportOrderSummaryStep)
                .listener(listener)
                .build();
    }

}
