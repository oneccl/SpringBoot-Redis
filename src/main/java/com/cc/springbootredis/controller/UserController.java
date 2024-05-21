package com.cc.springbootredis.controller;

import com.cc.springbootredis.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2022/12/6
 * Time: 19:34
 * Description:
 */
// Redis
/*
1、Redis是基于key-value存储的NoSQL系统(区别于MySQL的二维表格的形式存储)
2、Redis特点
  1）Redis读取的速度是110000次/s，写的速度是81000次/s
  2）原子性，Redis的所有操作都是原子性的，不可分割
  3）支持多种数据结构：string(字符串)、list(列表)、hash(哈希)、set(集合)、zset(有序集合)
  4）持久化，支持集群部署
  5）支持过期时间，支持事务，消息订阅
3、Redis的使用
  1）编写Redis的配置类RedisConfig，配置类标明@Configuration，并开启缓存@EnableCaching
    注意：如果没有自定义Redis配置类，需要在启动类上开启缓存@EnableCaching
  2）在业务层(service)使用Redis缓存注解将从数据库查询到的结果进行缓存(同时保存到Redis)当下次
    请求查询时，如果缓存中存在，则直接从Redis缓存中读取返回；如果不存在，查询数据库再添加到缓存
    (1)@Cacheable() :一般用于查询方法上
    (2)@CachePut() :一般用于添加的方法上
    (3)@CacheEvict() :一般用于更新或删除的方法上
    注解的常用属性方法：
    value/cacheNames: 缓存名，指定数据存放到哪个命名空间
    key: 缓存的key,可以使用SpEL标签自定义缓存的key
4、Java操作Redis: 见本工程test/DemoRedis.java
 */
@RestController
public class UserController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @RequestMapping("/getUsers.do")
    public Object getUsers(String id){
        // 使用Redis工具类对象redisTemplate添加一个值为String类型的键值对
        ValueOperations vo = redisTemplate.opsForValue();
        vo.set("getUsersKey",id);

        // SpringBoot-Redis设置Redis-Key过期时间
        //redisTemplate.opsForValue().set("key","value", Duration.ofDays(1));

        return userService.findUsers(id);
    }

}
