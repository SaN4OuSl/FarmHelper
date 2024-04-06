package com.example.farmhelper.scheduler;

import com.example.farmhelper.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SynchronizeUsersSchedule {
    private final UserService userService;

    @Scheduled(cron = "0 0 10 * * *")
    public void syncUsers() {
        log.info("Method syncUsers started");

        userService.synchronizeUsersWithKeycloak();

        log.info("Method syncUsers finished");
    }
}
