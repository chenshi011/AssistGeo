package cn.swipeblade.assistgeo.rockdemo.rocketmq.normal;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by GOT.hodor on 2017/12/20.
 */

public class SomeProducer {

    private final Logger log = LoggerFactory.getLogger(SomeProducer.class);

    private String producerGroup = "SomeProducerGroup";

    private String namesrvAddr = "127.0.0.1:9876";

    private DefaultMQProducer producer;

    public void init() throws MQClientException {
        producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(String.valueOf(System.currentTimeMillis()));
        producer.setRetryTimesWhenSendFailed(3);

        producer.start();
    }

    public void destroy() {
        producer.shutdown();
    }

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }
}
