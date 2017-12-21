package cn.swipeblade.assistgeo.rockdemo.rocketmq.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionExecutorImpl implements LocalTransactionExecuter {

    private final Logger logger = LoggerFactory.getLogger(TransactionExecutorImpl.class);

    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message message, Object o) {
        try{
            logger.info("execute local transaction : {}", new String(message.getBody()));
            logger.info("execute local transaction " + o);

            String tags = message.getTags();

            switch (tags) {
                case "TransactionTag_ROLL_BACK":
                    return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }


        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
