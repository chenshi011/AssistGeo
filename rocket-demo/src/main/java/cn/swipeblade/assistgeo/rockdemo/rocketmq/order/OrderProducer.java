package cn.swipeblade.assistgeo.rockdemo.rocketmq.order;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by GOT.hodor on 2017/12/20.
 */

@PropertySource(value = "classpath:rocketmq.properties", encoding = "utf-8")
public class OrderProducer {

    private final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    @Value("${order-producer-group}")
    private String producerGroup;

    @Value("${name-server-address}")
    private String namesrvAddr;

    private DefaultMQProducer producer;

    public void init() {
        producer = new DefaultMQProducer(producerGroup);
    }

}
