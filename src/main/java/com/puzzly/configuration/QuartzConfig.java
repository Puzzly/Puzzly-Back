package com.puzzly.configuration;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  public JobFactory jobFactory() {
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setJobFactory(jobFactory);
    return factory;
  }

}
