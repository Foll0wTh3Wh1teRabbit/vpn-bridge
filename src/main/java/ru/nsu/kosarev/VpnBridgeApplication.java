package ru.nsu.kosarev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class VpnBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VpnBridgeApplication.class, args);
    }

}
