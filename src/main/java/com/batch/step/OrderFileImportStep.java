package com.batch.step;

import com.batch.entity.Order;
import com.batch.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderFileImportStep {
    final JobRepository jobRepository;
    final PlatformTransactionManager transactionManager;
    final OrderRepository orderRepository;

    @Value("${batch.input.file}")
    Resource inputFile;

    @Bean
    public ItemReader<Order> orderFileReader() {
        System.out.println("===> inputFile: " + inputFile);
        System.out.println("===> inputFile.exists(): " + inputFile.exists());

        return new FlatFileItemReaderBuilder<Order>()
                .name("orderFileReader")
                .resource(inputFile)
                .delimited()
                .names("customerId", "itemId", "itemPrice", "itemName", "purchaseDate")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Order.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<Order, Order> orderValidationProcessor() {
        return order -> {
            System.out.println("Validating order: " + order.getItemName() + " for customer: " + order.getCustomerId());
            return order;
        };
    }

    @Bean
    public ItemWriter<Order> orderDatabaseWriter() {
        RepositoryItemWriter<Order> writer = new RepositoryItemWriter<>();
        writer.setRepository(orderRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean(name = "importOrderStep")
    public Step importOrderStep() {
        return new StepBuilder("importOrderStep", jobRepository)
                .<Order, Order>chunk(10, transactionManager)
                .reader(orderFileReader())
                .processor(orderValidationProcessor())
                .writer(orderDatabaseWriter())
                .build();
    }

}
