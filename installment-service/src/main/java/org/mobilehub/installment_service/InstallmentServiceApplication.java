package org.mobilehub.installment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class InstallmentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InstallmentServiceApplication.class, args);
    }
}
