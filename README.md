
## nacos 的学习

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

#### nacos spring 看文档


## spring cloud alibaba sentinel 学习

* sentinel 控制台安装和服务连接：https://blog.csdn.net/weixin_37677822/article/details/87697076
  * (1)启动控制台：
    * 在github 下载zip文件，本地解压，git-bash 访问文件到sentinel-dashboard项目下
    * mvn clean package 打jar包，在dashboard目录下的target目录下会有此jar包
    * java -jar sentinel-dashboard.jar --server.port=8084 启动此jar（即控制台项目） 
    * 访问http://localhost:8084/#/dashboard/ 即可查看控制台，目前是空的
  * (2)服务连接：
    ````
    pom加：
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        <version>0.2.1.RELEASE</version>
    </dependency>
    
    application.properties:
    
    spring.cloud.sentinel.transport.port= 8719
    spring.cloud.sentinel.transport.dashboard=127.0.0.1:8084

    ````
  * (3)效果测试：
     * 创建ProviderClient 接口和抽象方法test，绑定service-provider服务，consumer启动类加@EnableFeignClients 开起feign调用功能
     * 生产者和消费者分别创建test方法，postman测试调用test方法
     * 再看sentinel控制台，就会有对应的数据了
     * 可以利用postman或其他工具进行压力测试，在dashboard配置相应的规则，看sentinel的流量控制等功能
     
  * (4)测试服务限流和服务降级的功能：
     * 注意：服务限流和服务降级在配置流控规则是不一样的，服务限流配置对应的请求路径，而服务降级配置在对应的调用资源路径，可以看控制台的簇点链路
     * 实现方法各种，可以自行尝试。
     * 参考链接：https://www.cnblogs.com/babycomeon/p/11516011.html
     
     * restTemplate 调用方式：
        * 需要在注入bean的位置加注解：    @SentinelRestTemplate 开启sentinel的支持
        * 服务限流：然后可以在sentinel控制台配置流控规则，在对应的请求路径上（count设为1方便测试），快速请求几次，返回Blocked by Sentinel (flow limiting)  说明限流成功
        * 服务降级：在注解后面添加@SentinelRestTemplate(blockHandler = "handleException", blockHandlerClass = ExceptionUtil.class) 并定义ExceptionUtil处理类和对他的handleException静态方法（注意方法的定义，看本来的注释）
     
     * feign 调用方式：
        * 在配置文件加入： feign.sentinel.enabled=true
        * Sentinel已经对做了整合，我们使用Feign的地方无需额外的注解。同时，@FeignClient注解中的所有属性，Sentinel都做了兼容。
        * 服务降级：只需要在@FeignClient(name = "service-provider",fallback = FeignFallBack.class) 并定义FeignFallBack回退处理类 和Hystrix类似
       
     * 注解使用方式： 
        * 参考链接：https://my.oschina.net/didispace/blog/3067836
        * 在应用主类中增加注解支持的配置：
            ````
              // 注解支持的配置Bean
              @Bean
              public SentinelResourceAspect sentinelResourceAspect() {
                  return new SentinelResourceAspect();
              }
            ````
        * 在需要通过Sentinel来控制流量的地方使用@SentinelResource注解 @SentinelResource(value="hello",blockHandler="handleException",blockHandlerClass=ExceptionUtil.class)
        * 在控制台为 value 资源名 为hello的路径 配置相应的限流规则或者降级熔断规则，ExceptionUtil为处理类 
       
  * (5)sentinel动态规则持久化配置：
    * 上面四点都是在控制台进行设置的，非持久化的，项目重启后，就不存在了，解决方案：
    * （1）我们可以调用sentinel-core的核心库在代码里设置规则，缺点显而易见，不易维护
    * （2）持久化配置--->动态规则扩展：
      * https://www.cnblogs.com/didispace/p/10721802.html
      * https://github.com/alibaba/Sentinel/wiki/%E5%8A%A8%E6%80%81%E8%A7%84%E5%88%99%E6%89%A9%E5%B1%95
    ````
    以nacos配置中心为例，pom需添加依赖：
    
    <dependency>
        <groupId>com.alibaba.csp</groupId>
        <artifactId>sentinel-datasource-nacos</artifactId>
        <version>1.5.2</version>
    </dependency>
    
    application.properties:
    
    # 将sentinel等相关规则配置在nacos
    # 动态数据源： 支持nacos file apollo zk redis
    spring.cloud.sentinel.datasource.ds.nacos.server-addr=127.0.0.1:8848
    spring.cloud.sentinel.datasource.ds.nacos.dataId=${spring.application.name}-sentinel
    spring.cloud.sentinel.datasource.ds.nacos.groupId=DEFAULT_GROUP
    #在0.2.1版本中没有此配置
    #spring.cloud.sentinel.datasource.ds.nacos.rule-type=flow
    
    登录nacos客户端，新建配置：
    [
        {
            "resource": "/hello",
            "limitApp": "default",
            "grade": 1,
            "count": 5,
            "strategy": 0,
            "controlBehavior": 0,
            "clusterMode": false
        }
    ]
    resource：资源名，即限流规则的作用对象
    limitApp：流控针对的调用来源，若为 default 则不区分调用来源
    grade：限流阈值类型（QPS 或并发线程数）；0代表根据并发数量来限流，1代表根据QPS来进行流量控制
    count：限流阈值
    strategy：调用关系限流策略
    controlBehavior：流量控制效果（直接拒绝、Warm Up、匀速排队）
    clusterMode：是否为集群模式
    
    ````
    * (6)重启项目,打印一下日志说明成功，之前一开始启动报错，说什么方法找不到，貌似是版本的问题，换成1.5.2就好了，之前是1.4.0
    ````
    2019-04-16 14:24:42.919  INFO 89484 --- [           main] o.s.c.a.s.c.SentinelDataSourceHandler    : [Sentinel Starter] DataSource ds-sentinel-nacos-datasource start to loadConfig
    2019-04-16 14:24:42.938  INFO 89484 --- [           main] o.s.c.a.s.c.SentinelDataSourceHandler    : [Sentinel Starter] DataSource ds-sentinel-nacos-datasource load 1 FlowRule
    ````
    
  * 在完成了(5)(6)上面的整合之后，对于接口流控规则的修改就存在两个地方了：Sentinel控制台、Nacos控制台。
  * 这个时候，需要注意当前版本的Sentinel控制台不具备同步修改Nacos配置的能力，而Nacos由于可以通过在客户端中使用Listener来实现自动更新。所以，在整合了Nacos做规则存储之后，需要知道在下面两个地方修改存在不同的效果：
     * Sentinel控制台中修改规则：仅存在于服务的内存中，不会修改Nacos中的配置值，重启后恢复原来的值。
     * Nacos控制台中修改规则：服务的内存中规则会更新，Nacos中持久化规则也会更新，重启后依然保持。

* 2.Sentinel：阿里巴巴研发的分布式系统流量哨兵。
   * (1)官方文档：https://github.com/alibaba/Sentinel/wiki/%E4%BB%8B%E7%BB%8D
   * (2)主要功能：
     *  ①流量控制、
     *  ②熔断降级、
     *  ③系统负载保护、
     *  ④实时数据监控等
  * (3)构成：核心库和控制台。
  * (4)使用方法：
    * ①定义规则，就是定义某个资源保护的规则，比如限流策略、降级策略等
    * ②访问资源
    
 * 3.Sentinel 和 Hystrix 的对比： https://yq.aliyun.com/articles/623424
 
 
 
 
