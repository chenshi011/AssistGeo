package cn.swipeblade.assistgeo.rockdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by GOT.hodor on 2017/12/20.
 */

@SpringBootApplication
@ComponentScan(basePackages = {"cn.swipeblade.assistgeo.rockdemo"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
