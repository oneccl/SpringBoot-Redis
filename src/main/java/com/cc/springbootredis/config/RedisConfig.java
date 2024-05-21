package com.cc.springbootredis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2022/12/6
 * Time: 19:36
 * Description:
 */

// Redis缓存问题
/*
1、缓存击穿：Redis缓存中热点key失效，数据库中对应数据存在
  1）对于热点数据key(多并发访问)，某一时刻，大量线程请求热点资源，由于缓存该热点key失效
  导致后台数据库QPS(Query Pre Second,每秒执行查询次数)瞬间增大，这种现象称为缓存击穿
  2）避免：
  ①改变过期时间：设置热点数据永不过期
  ②分布式锁：重新设计缓存的方式
  对后台热点资源加锁，当并发线程因热点key失效而涌入后台时，第一个线程进入热点资源方法查询
  数据库(其它线程进入等待状态)，并将查询到的结果缓存到Redis，释放锁，其它线程查询Redis
2、缓存穿透：Redis缓存中不存在该数据，数据库中对应数据也不存在
  1）用户请求都不存在的数据，数据库会返回null，如果此类请求过多，会造成数据库压力增大，这种现象称为缓存穿透
  2）避免：使用过滤器和拦截器，如果请求结果为null的资源，直接拒绝返回
3、缓存雪崩：Redis缓存中大量key同时过期，数据库中对应数据存在
  1）后台数据库访问压力突然呈指数增长，甚至挂掉，这种现象称为缓存雪崩
  2）避免：设置热点数据永不过期；设置不同key的随机过期，避免集中过期
*/

// Redis配置类
@Configuration
// 开启缓存
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    // 自定义key规则
    // 缓存对象中，缓存是以key-value形式保存
    // 当不指定缓存的key时，SpringBoot会使用keyGenerator()生成key
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Bean
    RedisCacheWriter writer(RedisTemplate<String, Object> redisTemplate) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
    }

    // 自定义redisTemplate对象实例化规则
    @SuppressWarnings("all")
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 为了开发方便，一般直接使用<String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate();
        // 设置连接工厂，源码默认就可
        template.setConnectionFactory(factory);

        // json序列化配置
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类如String,Integer等会抛出出异常
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // string的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value也采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 将所有配置set进配置文件中
        template.afterPropertiesSet();
        return template;
    }

    // 自定义缓存管理器(缓存管理规则)
    @Bean
    CacheManager cacheManager(RedisCacheWriter writer) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();

        // 配置序列化（解决乱码的问题）设置过期时间1小时
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        // 配置list缓存的过期时间为120S
        configurationMap.put("list", config.entryTtl(Duration.ofSeconds(120)));

        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        return RedisCacheManager.builder(writer)
                .initialCacheNames(configurationMap.keySet())
                .withInitialCacheConfigurations(configurationMap)
                // 其他缓存过期时间为500S
                .cacheDefaults(config.entryTtl(Duration.ofSeconds(500)))
                .build();
    }

}
