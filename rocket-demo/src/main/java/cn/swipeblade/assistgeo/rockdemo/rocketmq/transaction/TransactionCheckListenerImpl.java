package cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionCheckListenerImpl implements TransactionCheckListener {

    private final Logger log = LoggerFactory.getLogger(TransactionCheckListenerImpl.class);

    @Override
    public LocalTransactionState checkLocalTransactionState(MessageExt messageExt) {
        log.info("check " + messageExt.toString());
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
