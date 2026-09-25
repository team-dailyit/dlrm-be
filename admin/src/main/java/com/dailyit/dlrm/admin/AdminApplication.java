package com.dailyit.dlrm.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.dailyit.dlrm.admin", "com.dailyit.dlrm.core"})
@EntityScan("com.dailyit.dlrm.core.domain")
@EnableJpaRepositories("com.dailyit.dlrm.core.repository")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
