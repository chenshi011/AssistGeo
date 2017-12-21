package cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionProducer {

    private final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private String producerGroup = "TransactionProducerGroup";

    private String namesrvAddr = "127.0.0.1:9876";

    private TransactionCheckListener listener;

    private TransactionMQProducer producer;

    public void init() throws MQClientException {
        listener = new TransactionCheckListenerImpl();
        producer = new TransactionMQProducer(producerGroup);

        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(String.valueOf(System.currentTimeMillis()));
        producer.setRetryTimesWhenSendAsyncFailed(3);

        producer.setCheckThreadPoolMinSize(2);
        producer.setCheckThreadPoolMaxSize(2);

        producer.setCheckRequestHoldMax(2000);
        producer.setTransactionCheckListener(listener);

        producer.start();
        log.info("producer started");

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
