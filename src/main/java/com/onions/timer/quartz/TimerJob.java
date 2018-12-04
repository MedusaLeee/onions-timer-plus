package com.onions.timer.quartz;
import com.onions.timer.service.TimerService;
import lombok.Getter;
import lombok.Setter;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@Setter
public class TimerJob extends QuartzJobBean {
    private String message;
    private Date startAt;
    private String queueName;
    private Logger log = LoggerFactory.getLogger(TimerJob.class);
    @Autowired
    private TimerService timerService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        String jobId = jobExecutionContext.getJobDetail().getKey().getName();
        log.info("execute: " + this.message);
        timerService.sendMessage(this.message, jobId);
    }
}
