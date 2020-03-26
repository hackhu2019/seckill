package com.hackhu.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.mq.RocketMQProducer;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.OrderService;
import com.hackhu.seckill.service.PromoService;
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
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
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
    @Resource
    private PromoService promoService;
    /**
     * 获取秒杀令牌
     *
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "getSeckillToken", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})

    public CommonReturnType getSeckillToken(@RequestParam(name = "itemId") Integer itemId,
                                            @RequestParam(name = "promoId") Integer promoId) throws BusinessException {
        // 根据 token 获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        // 获取秒杀令牌
        String seckillToken = promoService.generateSeckillToken(promoId, itemId, userModel.getId());
        if (seckillToken == null) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "秒杀令牌生成失败");
        }
        return CommonReturnType.create(seckillToken);
    }

    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "promoToken", required = false) String promoToken) throws BusinessException {

        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin.booleanValue()) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }

        //获取用户的登陆信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        if (userModel == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        if (promoId != null) {
            String redisSeckillToken = (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_userid_" + userModel.getId() + "_itemid_" + itemId);
            if (redisSeckillToken == null) {
                throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "令牌生成失败");
            }
            if (StringUtils.equals(promoToken, redisSeckillToken)) {
                throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "令牌生成失败");
            }
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
