package com.onions.timer.config;

import com.onions.timer.quartz.QuartzJobFactory;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class QuartzConfig {

    @Autowired
    DataSource dataSource;

    @Value("${self.quartz.properties-name}")
    String quartzPropertiesName;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(QuartzJobFactory quartzJobFactory) throws Exception {
        System.out.println("quartzJobFactory = [" + quartzPropertiesName + "]");
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(quartzJobFactory);
        factoryBean.setConfigLocation(new ClassPathResource(quartzPropertiesName));
        factoryBean.setDataSource(dataSource);
        factoryBean.afterPropertiesSet();
        return factoryBean;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.start();
        System.out.println("schedulerFactoryBean = [ start ....]");
        return scheduler;
    }

}