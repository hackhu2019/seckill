package com.hackhu.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.hackhu.seckill.dao.ItemStockDTOMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hackhu
 * @date 2020/3/19
 */
@Component
public class RocketMQConsumer {
    private DefaultMQPushConsumer rocketMQConsumer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.nameserver.topicname}")
    private String topicName;
    @Resource
    private ItemStockDTOMapper itemStockDTOMapper;
    @PostConstruct
    public void init() throws MQClientException {
        // rocketMQConsumer 初始化
        rocketMQConsumer = new DefaultMQPushConsumer("stock_consumer_group");
        rocketMQConsumer.setNamesrvAddr(nameAddr);
        rocketMQConsumer.subscribe(topicName, "*");
        rocketMQConsumer.registerMessageListener(
                (List<MessageExt> megs, ConsumeConcurrentlyContext context) -> {
                    // 在数据库中减内存
                    Message msg = megs.get(0);
                    String jsonString = new String(msg.getBody());
                    Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
                    Integer itemId = (Integer) map.get("itemId");
                    Integer amount = (Integer) map.get("amount");
                    itemStockDTOMapper.decreaseStock(itemId, amount);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                });
        rocketMQConsumer.start();
    }
}
