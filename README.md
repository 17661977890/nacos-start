
# nacos 的学习

#### 官方文档： https://nacos.io/zh-cn/docs

#### nacos与Eureka注册中心的比较，与apollo配置中心的比较： https://www.jianshu.com/p/afd7776a64c6

#### 注意事项1：
````
SpringCloudConfig和 NacosConfig这种统一配置服务在springboot项目中初始化时，
都是加载bootstrap.properties配置文件去初始化上下文。
这是由spring boot的加载属性文件的优先级决定的，
想要在加载属性之前去config server上取配置文件，
那NacosConfig或SpringCloudConfig相关配置就是需要最先加载的，
而bootstrap.properties的加载是先于application.properties的，
所以config client要配置config的相关配置就只能写到bootstrap.properties里了
如果是application文件 则启动项目会报   nacosException:null
````

#### 注意事项2：

* (1)关于springBoot、springCloud、springCloudAlibaba 版本对应关系错误导致服务注册失败！！

* (2)版本对应关系：(在顶层父pom，我们定义了springboot的版本为2.0.4，所以对应的nacos-spring-cloud 中要注意对应的版本引入)

|cloud Release |Boot Version|Spring Cloud Alibaba
|------|------|-----|
|Greenwich | 2.1.x | 0.2.2(还没有RELEASE)
|Finchley |	2.0.x |	0.2.1
|Edgware |	1.5.x |	0.1.1
|Dalston |	1.5.x |	0.1.1

* spring-cloud-starter-alibaba-nacos-discovery 的版本关系参考spring boot的版本 2.x 对应 0.2.x | 1.x 对应 0.1.x

* (3)服务注册成功的日志打印，另外可以登录http://localhost:8848/nacos/ nacos后台查看服务列表 账户密码：nacos
``
2019-11-15 17:22:37.387  INFO 34012 --- [  restartedMain] o.s.c.a.n.registry.NacosServiceRegistry  : nacos registry, service-provider 192.168.1.66:8084 register finished
``

#### 注意事项3:
* 在官方文档给出的案例中，我们可以看到如下依赖，目的是：
* 如果 nacos-client 升级了，对应的 spring-cloud 客户端版本不一定也同步升级，这个时候可以采用如下的方式强制升级 nacos-client

````
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!--此依赖是nacos的java SDK,提供了关于服务端配置管理和服务发现的相关接口： NacosFactory NamingFactory，详情open api可参考官方文档-->
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>[latest version]</version>
</dependency>
````

#### nacos spring 看文
