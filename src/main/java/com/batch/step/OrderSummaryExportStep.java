package com.batch.step;

import com.batch.entity.Order;
import com.batch.entity.OrderSummary;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class OrderSummaryExportStep {

    JobRepository jobRepository;
    PlatformTransactionManager transactionManager;
    EntityManagerFactory entityManagerFactory;

    @Bean
    public ItemReader<Order> orderDatabaseReader() {
        JpaPagingItemReader<Order> reader = new JpaPagingItemReader<>();
        reader.setQueryString("SELECT o FROM Order o");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(10);
        return reader;
    }

    @Bean
    public ItemProcessor<Order, OrderSummary> highValueOrderProcessor() {
        return order -> {
            if (order.getItemPrice() <= 13_000_000) {
                System.out.println("Skipping low-value order: " + order);
                return null;
            }

            System.out.println("Processing high-value order: " + order);
            return OrderSummary.builder()
                    .id(order.getId())
                    .customerId(order.getCustomerId())
                    .itemName(order.getItemName())
                    .itemPrice(order.getItemPrice())
                    .build();
        };
    }

    @Bean
    public ItemWriter<OrderSummary> orderSummaryFileWriter() {
        return new FlatFileItemWriterBuilder<OrderSummary>()
                .name("orderSummaryWriter")
                .resource(new FileSystemResource("output/orders_summary.csv"))
                .delimited()
                .delimiter(",")
                .names("customerId", "itemName", "itemPrice")
                .headerCallback(writer -> writer.write("Customer ID, Item Name, Item Price"))
                .build();
    }

    @Bean(name = "exportOrderSummaryStep")
    public Step exportOrderSummaryStep() {
        return new StepBuilder("exportOrderSummaryStep", jobRepository)
                .<Order, OrderSummary>chunk(10, transactionManager)
                .reader(orderDatabaseReader())
                .processor(highValueOrderProcessor())
                .writer(orderSummaryFileWriter())
                .build();
    }
}
