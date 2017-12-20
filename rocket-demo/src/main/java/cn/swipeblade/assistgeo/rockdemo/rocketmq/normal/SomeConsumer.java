package cn.swipeblade.assistgeo.rockdemo.rocketmq.normal;

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

/**
 * Created by GOT.hodor on 2017/12/20.
 */

@PropertySource(value = "classpath:rocketmq.properties", encoding = "utf-8")
public class SomeConsumer {
    private static final Logger log = LoggerFactory.getLogger(SomeConsumer.class);

    @Value("${some-comsumer-group}")
    private String consumerGroup;

    @Value("${name-server-address}")
    private String namesrvAddr;

    private DefaultMQPushConsumer consumer;

    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(String.valueOf(System.currentTimeMillis()));

        consumer.subscribe("SomeTopic", "SomeTag");

        //设置首次启动从队列头部消费还是尾部消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //设置为集群消费
        consumer.setMessageModel(MessageModel.CLUSTERING);

        consumer.registerMessageListener((MessageListenerConcurrently)(msgs, context)->{
            MessageExt msg = msgs.get(0);

            try{
                String topic = msg.getTopic();
                switch (topic) {
                    case "SomeTopic":
                        String tags = msg.getTags();
                        switch (tags) {
                            case "SomeTag":
                                log.info(new String(msg.getBody(), "utf-8"));
                                break;
                        }
                        break;
                }
            }catch (Exception e) {
                if (msg.getReconsumeTimes() == 3) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
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
