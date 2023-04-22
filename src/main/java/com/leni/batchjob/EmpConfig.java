package com.leni.batchjob;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmpConfig {

	@Bean
	public FlatFileItemReader<Employee> reader(){
		return new FlatFileItemReaderBuilder<Employee>()
				.name("empReader")
				.resource(new ClassPathResource("MOCK_DATA.csv"))
				.delimited()
				.names(new String[] {"id","name","dept","salary"})
				.linesToSkip(1)
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
					setTargetType(Employee.class);
				}})
				.build();
	}
	
	@Bean
	public EmpItemProcessor processor() {
		return new EmpItemProcessor();
	}
	
	@Bean
	public JdbcBatchItemWriter<Employee> writer(DataSource dataSource){
		return new JdbcBatchItemWriterBuilder<Employee>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("insert into employee(id,name,dept,salary) values(:id,:name,:dept,:salary)")
				.dataSource(dataSource)
				.build();
	}
	
	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Employee> writer) {
		return new StepBuilder("step1", jobRepository)
				.<Employee, Employee>chunk(100, transactionManager)
				.reader(reader())
				.processor(processor())
				.writer(writer)
				.build();
	}
	
	@Bean
	public Job jobRunner(JobRepository jobRepository, EmpListener listener, Step step1) {
		return new JobBuilder("EmpJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1)
				.end()
				.build();
	}
}
