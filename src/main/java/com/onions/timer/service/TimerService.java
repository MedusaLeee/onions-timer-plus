package com.onions.timer.service;

import org.springframework.stereotype.Service;

@Service
public class TimerService {
    public void sendMessage(String message) {
        System.out.println("TimerService sendMessage = [" + message + "]");
    }
}
