package com.hackhu.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.google.common.util.concurrent.RateLimiter;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.mq.RocketMQProducer;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.OrderService;
import com.hackhu.seckill.service.PromoService;
import com.hackhu.seckill.service.model.UserModel;
import com.hackhu.seckill.utils.CodeUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

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
    private ExecutorService executorService;
    private RateLimiter orderCreateRateLimiter;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(20);
        orderCreateRateLimiter = RateLimiter.create(10);
    }

    /**
     *
     */
    @RequestMapping(value = "/getSeckillToken", method = {RequestMethod.POST, RequestMethod.GET})
    public void getSeckillToken(HttpServletResponse response) throws BusinessException, IOException {
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        Map<String, Object> map = CodeUtils.generateCodeAndPic();
        redisTemplate.opsForValue().set("verify_code_" + userModel.getId(), map.get("code"));
        redisTemplate.expire("verify_code_" + userModel.getId(), 10, TimeUnit.MINUTES);
        ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", response.getOutputStream());
    }
    /**
     * 获取秒杀令牌
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/getSeckillToken", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getSeckillToken(@RequestParam(name = "itemId") Integer itemId,
                                            @RequestParam(name = "promoId") Integer promoId,
                                            @RequestParam(name = "verifyCode") String verifyCode) throws BusinessException {
        // 根据 token 获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_LOGIN);
        }
        // 检测验证码有效性
        String redisVerifyCode = (String) redisTemplate.opsForValue().get("verify_code_" + userModel.getId());
        if (!StringUtils.equals(redisVerifyCode, verifyCode)) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "验证码不合法");
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
        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);

        return CommonReturnType.create(orderModel);
    }
}
