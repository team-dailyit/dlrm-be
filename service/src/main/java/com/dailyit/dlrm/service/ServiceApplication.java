package com.dailyit.dlrm.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.dailyit.dlrm.service", "com.dailyit.dlrm.core"})
@EntityScan("com.dailyit.dlrm.core.domain")
@EnableJpaRepositories("com.dailyit.dlrm.core.repository")
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
