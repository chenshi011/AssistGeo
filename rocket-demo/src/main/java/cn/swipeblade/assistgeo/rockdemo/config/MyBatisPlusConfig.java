package cn.swipeblade.assistgeo.rockdemo.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by GOT.hodor on 2017/12/19.
 */

@Configuration
@MapperScan(basePackages = {"cn.swipeblade.bootdemo.mapper"})
public class MyBatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}

