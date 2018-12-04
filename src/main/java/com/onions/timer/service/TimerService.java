package com.onions.timer.service;

import com.alibaba.fastjson.JSONObject;
import com.onions.timer.mq.TimerProducer;
import com.onions.timer.quartz.TimerJob;
import com.onions.timer.utils.Utils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TimerService {
    private Logger log = LoggerFactory.getLogger(TimerService.class);
    @Autowired
    private TimerProducer timerProducer;
    @Autowired
    private Scheduler scheduler;

    public void sendMessage(String message, String jobId) {
        timerProducer.send(message, jobId);
    }

    public void setJob(String message) {
        log.info("setJob: " + message);
        JSONObject messageObject = Utils.jsonStringToJSONObject(message);
        long startAt = messageObject.getLongValue("startAt");
        log.info("basicConsume startAt: " + startAt);
        String uuid = Utils.getUuid();
        JobDetail jobDetail = JobBuilder.newJob(TimerJob.class)
                .withIdentity("job:"+ uuid, "onion")
                .usingJobData("message", message)
                .build();
        log.info("job time: " + Utils.formatDate(new Date(startAt)));
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger:"+ uuid, "onion")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()) // 失效立即执行
                .startAt(new Date(startAt)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }
}
