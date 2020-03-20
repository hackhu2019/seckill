package com.hackhu.seckill.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author hackhu
 * @date 2020/3/19
 */
@Component
public class RocketMQConsumer {
    private DefaultMQPushConsumer rocketMQConsumer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;

    @PostConstruct
    public void init() throws MQClientException {
        // rocketMQConsumer 初始化
        rocketMQConsumer = new DefaultMQPushConsumer("stock_consumer_group");
        rocketMQConsumer.setNamesrvAddr(nameAddr);
        rocketMQConsumer.subscribe(topicName, "*");
        rocketMQConsumer.registerMessageListener(
                (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> ConsumeConcurrentlyStatus.CONSUME_SUCCESS);
    }
}
