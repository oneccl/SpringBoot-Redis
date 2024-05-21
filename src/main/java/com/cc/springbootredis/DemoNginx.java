package com.cc.springbootredis;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2022/12/9
 * Time: 15:19
 * Description:
 */
public class DemoNginx {
    // Nginx
    /*
    1、Nginx：是一个高性能的http和反向代理Web服务器
    2、Nginx代理
    代理：Java的一种设计模式：给定一个对象的代理对象，并由代理对象控制原对象的引用
    Nginx代理：主要做http/https代理服务器
    3、正向代理：代理客户端
    客户端向代理服务器发送请求，代理服务器指定目标服务器，由该目标服务器返回数据转交给客户端
    例如：VPN: 中转服务；当电脑接入VPN后，电脑对外IP地址就会变成VNP服务的公网IP地址，如访问Google
    先连上VPN服务器，VPN服务器会将电脑的IP地址转换为美国的IP地址，就可以访问Google了
    4、反向代理：代理服务器
    客户端对代理是无感知的，客户端不需要任何配置就可以访问，客户端只需要将请求发送到反向代理服务器
    由反向代理服务器去选择目标服务器获取数据后，再返回给客户端，此时反向代理服务器和目标服务器对外就是一个服务器
    只不过暴露的是代理服务器地址，隐藏了真实服务器IP地址
    5、Nginx的使用
     1）下载Nginx: http://nginx.org/en/download.html
     2）启动Nginx,检查Nginx是否启动成功
     3）配置Nginx解压的包下的配置文件：nginx-1.20.2\nginx-1.20.2\conf\nginx.conf
     详见本地：E:\BDsoftware\nginx\nginx-1.20.2\nginx-1.20.2\conf\nginx.conf
    6、Nginx的cmd常用指令: 详见本地：E:\BDsoftware\nginx\下载nginx.md
    7、Nginx反向代理相关指令: 详见nginx.conf配置文件
    8、nginx.conf配置文件部分内容：
    **************************************************************
    ......

    # 服务集群(Java工程: SpringBoot-Thymeleaf)
    upstream bootCluster{
        # 负载均衡策略

        # 1）均匀派发（轮循）：负载均衡器平均派发任务给服务器
        server localhost:8080;
        server localhost:8081;

        # 2）加权派发：使用weight比例，服务器处理能力不同，分配任务权重不同
        #server localhost:8080 weight=3;
        #server localhost:8081 weight=1;

        # 3）最小连接数策略
        # 关键字
        #least_conn;
        #server localhost:8080;
        #server localhost:8081;
    }

    # Nginx反向代理的使用
    # Nginx反向代理相关指令
    server {
        # 1、listen: 网络监听配置，语法格式：
        # 监听IP地址：listen ip/address
        # 监听端口：listen port
        # 监听IP地址和端口：listen ip:port
        listen       80;
        # 2、server_name: 虚拟主机配置，语法格式：
        # server_name name/ip地址
        server_name  localhost;

        ......

        # 3、location: 用于配置url,语法格式：
        # location url {}
        location / {
            root   html;
            index  index.html index.htm;
        }

        # 例：配置百度的代理服务(http://localhost/baidu/)
        location /baidu/ {
        # 4、proxy_pass：用于设置被代理服务器的地址，语法格式
        # proxy_pass url 或 协议+主机+IP+Port
            proxy_pass   https://www.baidu.com/;
            # 5、index: 用于设置被代理服务器的地址的默认首页，语法格式(多个页面用空格隔开)：
            # index 页面1 页面2 ...
            index  index.html index.jsp;
        }

        # 代理Tomcat服务(Java工程: SpringBoot-Thymeleaf)
        location /springbootThymeleaf/ {
            proxy_pass   http://localhost:8080/;
            index  index.html login.html;
        }

        # 访问server外面的服务集群：upstream bootThymeleaf{}
        # (Java工程: SpringBoot-Thymeleaf)
        location /bootThymeleaf/ {
            proxy_pass   http://bootCluster/;
            index  index.html login.html;
        }

        ......
    }
    ......
    **************************************************************
     */

}
