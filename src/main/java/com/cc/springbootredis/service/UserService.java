package com.cc.springbootredis.service;

import com.cc.springbootredis.dao.UserMapper;
import com.cc.springbootredis.pojo.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2022/12/6
 * Time: 19:33
 * Description:
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    // @Cacheable：将查询结果的key添加到redis缓存
    // keyList命名/缓存空间用于存储所有的key
    // 根据Redis配置文件Key生成器，生成的查询结果的key为：
    // keyList::com.cc.springbootredis.service.UserServicefindUsers2
    // key为：@Cacheable注解所在的方法的所在全类名+方法名+参数值
    @Cacheable(value = "keyList")
    public List<User> findUsers(String id){
        return userMapper.queryUsers(id);
    }

}
