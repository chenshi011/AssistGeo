package cn.swipeblade.assistgeo.rockdemo.rocketmq;

import cn.swipeblade.assistgeo.rockdemo.rocketmq.normal.SomeConsumer;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.normal.SomeProducer;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.order.OrderConsumer;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.order.OrderProducer;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction.TransactionConsumer;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction.TransactionProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by GOT.hodor on 2017/12/20.
 */

@Configuration
@ComponentScan(value = "cn.swipeblade.assistgeo.rockdemo.rocketmq")
@PropertySource(value = "classpath:rocketmq.properties", encoding = "utf-8")
public class ConsumerConfig {

    @Value("${name-server-address}")
    private String namesrvAddr;

    @Value("${some-consumer-group}")
    private String someConsumerGroup;

    @Value("${some-producer-group}")
    private String someProducerGroup;

    @Value("${order-consumer-group}")
    private String orderConsumerGroup;

    @Value("${order-producer-group}")
    private String orderProducerGroup;

    @Value("${transaction-consumer-group}")
    private String transactionConsumerGroup;

    @Value("${transaction-producer-group}")
    private String transactionProducerGroup;

    //region normal
    @Bean(initMethod = "init", destroyMethod = "destroy")
    SomeConsumer someConsumer() {
        return new SomeConsumer();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    SomeProducer someProducer() {
        return new SomeProducer();
    }

    //endregion

    //region Orderly
    @Bean(initMethod = "init", destroyMethod = "destroy")
    OrderConsumer orderConsumer() {
        return new OrderConsumer();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    OrderProducer orderProducer() {
        return new OrderProducer();
    }
    //endregion

    //region transaction
    @Bean(initMethod = "init", destroyMethod = "destroy")
    TransactionConsumer transactionConsumer() {
        return new TransactionConsumer();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    TransactionProducer transactionProducer() {
        return new TransactionProducer();
    }

    //endregion

}
