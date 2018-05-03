package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-25 15:36
 **/
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class LeyouPagesApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyouPagesApplication.class, args);
    }
}
