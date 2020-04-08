seckill 是一个面向中小企业的电商秒杀系统
# 系统介绍
本系统是基于 SpringBoot 开发的高并发限时抢购秒杀系统，实现基本的登录、查看商品列表、秒杀、下单等功能，项目中还针对高并发情况实现了系统缓存、流量削峰和防刷限流。

# 开发工具
IntelliJ IDEA + Navicat + VS Code + Git + Chrome

# 压测工具
JMeter

# 开发技术
后端技术 ：SpringBoot + MyBatis + MySQL

中间件技术 : Redis + RabbitMQ + Guava

# 主要技术点
  1、对用户信息、商品信息等常用数据进行缓存处理
    
  2、采用异步流程对交易模块进行优化，维护数据最终一致性
    
  3、对秒杀模块引入令牌进行流量削峰、防刷限流
    
  4、全局异常同意处理