package com.cybergamer.hardware_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HardwareMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(HardwareMonitorApplication.class, args);
    }

}
