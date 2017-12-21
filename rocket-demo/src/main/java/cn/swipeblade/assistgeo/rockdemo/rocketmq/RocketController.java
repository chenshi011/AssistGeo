package cn.swipeblade.assistgeo.rockdemo.rocketmq;

import cn.swipeblade.assistgeo.rockdemo.rocketmq.normal.SomeProducer;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.order.OrderProducer;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction.TransactionExecutorImpl;
import cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction.TransactionProducer;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RocketController {

    private final Logger logger = LoggerFactory.getLogger(RocketController.class);

    @Autowired
    SomeProducer someProducer;

    @Autowired
    OrderProducer orderProducer;

    @Autowired
    TransactionProducer transactionProducer;

    //region 普通无序消息
    @GetMapping(value = "/normal/hello")
    public void sendMessage(String msg) {
        Message message = new Message("SomeTopic", "SomeTag", (JSONObject.toJSONString(msg)).getBytes());
        SendResult sendResult = null;

        try{
            sendResult = someProducer.getProducer().send(message);
        }catch (RemotingException | MQClientException | MQBrokerException | InterruptedException e) {
            logger.error("occurred exception: type -> {}, message -> {}", "RemotingException", e.getMessage());
        }

        if (sendResult == null || sendResult.getSendStatus() == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
            logger.error("producer send message failed");
        }
    }
    //endregion

    //region 顺序消息
    @GetMapping(value = "/order/hello")
    public void sendOrderMessage(String msg) {
        String[] tags = new String[]{"createTag", "executeTag", "sendTag", "destroyTag"};

        for (int orderId=0; orderId < 10; orderId ++) {
            for (int type=0; type < tags.length; type++) {
                SendResult sendResult = null;
                try{
                    Message message = new Message(
                            "OrderTopic",
                            tags[type % tags.length],
                            orderId + ":" + type,
                            ("orderId : " +  type + " -> " + msg).getBytes());
                    sendResult = orderProducer.getProducer().send(
                            message,
                            (mqs, message1, args) -> {
                                Integer id = (Integer) args;
                                int index = id % mqs.size();
                                return mqs.get(index);
                            },
                            orderId
                        );
                }catch (RemotingException | MQBrokerException | InterruptedException | MQClientException e) {
                    logger.error("occurred exception: type -> {}, message -> {}", "RemotingException", e.getMessage());
                }
                logger.info(sendResult.toString());
            }

        }
        orderProducer.getProducer().shutdown();



    }
    //endregion

    //region 事务消息
    @GetMapping(value = "/transaction/hello")
    public void sendTransactionMessage(String msg) {
        TransactionExecutorImpl transactionExecutor = new TransactionExecutorImpl();

        try{
            Message message1 = new Message(
                    "TransactionTopic",
                    "TransactionTag1",
                    "KEY1",
                    ("hello mq 1" + msg).getBytes());
            Message message2 = new Message(
                    "TransactionTopic",
                    "TransactionTag2",
                    "KEY2",
                    ("hello mq 2" + msg).getBytes()
            );

            SendResult sendResult1 = transactionProducer.getProducer()
                    .sendMessageInTransaction(message1, transactionExecutor, null);

            SendResult sendResult2 = transactionProducer.getProducer()
                    .sendMessageInTransaction(message2, transactionExecutor, null);


        }catch (Exception e) {
            e.printStackTrace();
        }

        transactionProducer.getProducer().shutdown();
    }

    //endregion

}
