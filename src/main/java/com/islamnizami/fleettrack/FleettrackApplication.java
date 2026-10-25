package com.islamnizami.fleettrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FleettrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleettrackApplication.class, args);
    }

}
