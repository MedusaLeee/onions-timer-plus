package com.onions.timer.quartz;
import com.onions.timer.service.TimerService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component
@Data
public class TimerJob implements Job {
    private String message;
    private Date startAt;
    private String queueName;

    @Resource
    private TimerService timerService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        timerService.sendMessage(this.message);
    }
}
