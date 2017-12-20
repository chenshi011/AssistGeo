package cn.swipeblade.assistgeo.rockdemo.rocketmq.order;

import com.rabbitmq.client.DefaultConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.Random;

/**
 * Created by GOT.hodor on 2017/12/20.
 */

@PropertySource(value = "classpath:rocketmq.properties", encoding = "utf-8")
public class OrderConsumer {

    private final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @Value("${order-comsumer-group}")
    private String consumerGroup;

    @Value("${name-server-address}")
    private String namesrvAddr;

    private DefaultMQPushConsumer consumer;

    public void init() throws MQClientException {

        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(String.valueOf(System.currentTimeMillis()));

        consumer.subscribe("OrderTopic", "OrderTag");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);

        consumer.setConsumeThreadMin(5);
        consumer.setConsumeThreadMax(10);

        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            context.setAutoCommit(true);

            try{
                Thread.sleep(new Random().nextInt(1000));
                Message msg = msgs.get(0);
                log.info(new String(msg.getBody(), "utf-8"));
            }catch (Exception e) {
                e.printStackTrace();
            }
            return ConsumeOrderlyStatus.SUCCESS;
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
