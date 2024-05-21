package com.cc.springbootredis.dao;

import com.cc.springbootredis.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2022/12/6
 * Time: 19:32
 * Description:
 */
@Repository
public interface UserMapper {

    List<User> queryUsers(String id);

}
