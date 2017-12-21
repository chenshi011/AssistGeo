package cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

public class TransactionConsumer {

    private final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);

    private String consumerGroup = "TransactionConsumerGroup";

    private String namesrvAddr = "127.0.0.1:9876";

    private DefaultMQPushConsumer consumer;

    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(String.valueOf(System.currentTimeMillis()));

        consumer.subscribe("TransactionTopic", "*");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);

        consumer.setConsumeThreadMin(5);
        consumer.setConsumeThreadMax(10);

        consumer.registerMessageListener((MessageListenerConcurrently)(msgs, context) -> {
            try{
                for (MessageExt msg : msgs) {
                    logger.info(new String(msg.getBody(), "utf-8"));
                }
            }catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

    }

    public void destroy() {
        consumer.shutdown();
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }
}
