package com.cybergamer.hardware_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class HardwareMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(HardwareMonitorApplication.class, args);
    }

}
