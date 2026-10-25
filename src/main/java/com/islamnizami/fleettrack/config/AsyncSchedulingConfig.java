package com.islamnizami.fleettrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Turns on @Scheduled jobs (maintenance reminders, cleanup) and @Async methods (notifications). */
@Configuration
@EnableScheduling
@EnableAsync
public class AsyncSchedulingConfig {
}
