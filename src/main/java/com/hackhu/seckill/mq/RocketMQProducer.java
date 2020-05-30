package com.hackhu.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.hackhu.seckill.dao.StockLogDTOMapper;
import com.hackhu.seckill.dto.StockLogDTO;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.OrderService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hackhu
 * @date 2020/3/19
 */
@Component
public class RocketMQProducer {
    private DefaultMQProducer producer;
    private TransactionMQProducer transactionMQProducer;
    @Resource
    private OrderService orderService;
    @Resource
    private StockLogDTOMapper stockLogDTOMapper;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.nameserver.topicname}")
    private String topicName;

    @PostConstruct
    public void init() throws MQClientException {
        // mq producer 初始化
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();

        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();
        
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                // 创建订单
                Integer itemId = (Integer) ((Map) o).get("itemId");
                Integer promoId = (Integer) ((Map) o).get("promoId");
                Integer userId = (Integer) ((Map) o).get("userId");
                Integer amount = (Integer) ((Map) o).get("amount");
                String stockLogId = (String) ((Map) o).get("stockLogId");
                try {
                    orderService.createOrder(userId, itemId, promoId, amount, stockLogId);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    // 将对应的 stockLog 设为回滚状态
                    StockLogDTO stockLogDTO = stockLogDTOMapper.selectByPrimaryKey(stockLogId);
                    stockLogDTO.setStatus(3);
                    stockLogDTOMapper.updateByPrimaryKeySelective(stockLogDTO);
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // 根据扣减库存是否成功，返回不同状态
                String jsonString = new String(messageExt.getBody());
                Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
                String stockLogId = (String) map.get("stockLogId");
                StockLogDTO stockLogDO = stockLogDTOMapper.selectByPrimaryKey(stockLogId);
                if (stockLogDO == null) {
                    return LocalTransactionState.UNKNOW;
                }
                if (stockLogDO.getStatus().intValue() == 2) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (stockLogDO.getStatus().intValue() == 1) {
                    return LocalTransactionState.UNKNOW;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });
    }

    /**
     * 同步商品减库存消息
     */
    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        Message message = new Message(topicName, "decrease", JSON.toJSONString(bodyMap).getBytes(Charset.forName("UTF-8")));
        try {
            producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        } catch (RemotingException e) {
            e.printStackTrace();
            return false;
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 事务型同步减库存消息
     */
    public boolean transactionAsyncReduceStock(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) {

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        bodyMap.put("stockLogId", stockLogId);

        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("itemId", itemId);
        argsMap.put("amount", amount);
        argsMap.put("userId", userId);
        argsMap.put("promoId", promoId);
        argsMap.put("stockLogId", stockLogId);

        Message message = new Message(topicName, "increase",
                JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
        TransactionSendResult sendResult = null;
        try {
            sendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        if (sendResult.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
            return false;
        } else if (sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
            return true;
        } else {
            return false;
        }
    }
}
