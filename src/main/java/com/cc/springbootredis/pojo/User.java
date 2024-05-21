package com.cc.springbootredis.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2022/12/6
 * Time: 19:31
 * Description:
 */

@Data  // get()、set()方法
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer userId;
    private String userCode;
    private String userName;
    private String userPassword;
    private Integer gender;
    private Integer age;
    private String birthday;
    private String phone;
    private String address;
    private Integer userRole;

}
