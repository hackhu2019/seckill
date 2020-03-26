package com.hackhu.seckill.controller;

import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.mq.RocketMQProducer;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.OrderService;
import com.hackhu.seckill.service.model.OrderModel;
import com.hackhu.seckill.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author hackhu
 * @date 2020/3/1
 */
@RestController
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RocketMQProducer rocketMQProducer;
    @Resource
    private ItemService itemService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    //封装下单请求
    @RequestMapping(value = "/createorder",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name="itemId")Integer itemId,
                                        @RequestParam(name="amount")Integer amount,
                                        @RequestParam(name="promoId",required = false)Integer promoId) throws BusinessException {

        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin.booleanValue()){
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN,"用户还未登陆，不能下单");
        }

        //获取用户的登陆信息
        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");
        if (userModel == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        // 判断库存是否已售罄
        if (redisTemplate.hasKey("promo_item_stock_invalid_" + itemId)) {
            throw new BusinessException(BusinessErrorEnum.STOCK_NOT_ENOUGH);
        }
        // 库存流水初始化
        String stockLogId = itemService.initStockLog(itemId, amount);
        if (!rocketMQProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId)) {
            throw new BusinessException(BusinessErrorEnum.UNKNOWN_ERROR, "下单失败");
        }
        return CommonReturnType.create(null);
    }
}
