package cn.edu.neu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 *
 * @author 32098
 */
@MapperScan("cn.edu.neu.dao")
@SpringBootApplication
@EnableEurekaClient
public class StartupMain {
    public static void main(String[] args) {
        SpringApplication.run( StartupMain.class, args );
    }
}

