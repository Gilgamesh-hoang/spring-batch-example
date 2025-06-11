package com.batch.listener;

import com.batch.repo.OrderRepository;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    OrderRepository orderRepository;

    public JobCompletionNotificationListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("Job completed successfully! Processing orders...");
            System.out.println("Total Orders Processed: " + orderRepository.count());
        }else {
            System.out.println("Job failed with status: " + jobExecution.getStatus());
        }
    }
}