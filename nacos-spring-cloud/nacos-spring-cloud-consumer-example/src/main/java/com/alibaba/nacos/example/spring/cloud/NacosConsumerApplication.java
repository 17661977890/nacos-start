package com.alibaba.nacos.example.spring.cloud;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.alibaba.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author
 */
//开启feignclient 接口调用
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class NacosConsumerApplication {

    /**
     * （1）使用RestTemplate的时候需要增加@SentinelRestTemplate来开启Sentinel对RestTemplate的支持。
     *   SentinelRestTemplate注解的属性支持限流(blockHandler, blockHandlerClass)和降级(fallback, fallbackClass)的处理。
     *   其中blockHandler或fallback属性对应的方法必须是对应blockHandlerClass或fallbackClass属性中的静态方法。
     *   SentinelRestTemplate注解的限流(blockHandler, blockHandlerClass)和降级(fallback, fallbackClass)属性不强制填写。
     *
     * 服务限流：blockHandler
     *      blockHandler：处理 BlockException 的方法名，可选项。若未配置，则将 BlockException 直接抛出。
     *      blockHandlerClass ：如果你不想让异常处理方法跟业务方法在同一个类中，可以使用 blockHandlerClass 为对应的类的 Class 对象，
     *                          注意对应的函数必需为 static 函数，否则无法解析。
     *      在dashboard配置请求路径流控规则测试，返回Blocked by Sentinel (flow limiting)  说明限流成功
     *
     * 服务降级：fallback，fallbackClass
     *
     *      在dashboard配置资源路径流控规则测试默认返回：RestTemplate request block by sentinel
     *
     *      使用blockHandler做降级加回退类，可以返回自定义消息
     *
     * @return
     */
    @LoadBalanced
    @Bean
    @SentinelRestTemplate(fallback = "handleException", fallbackClass = ExceptionUtil.class)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     *  （2）配合注解使用 @SentinelResource(value="hello",blockHandler="handleException",blockHandlerClass=ExceptionUtil.class)
     *       value:资源名
     *       blockHandler：处理 BlockException 的方法名，可选项。若未配置，则将 BlockException 直接抛出。
     *       blockHandlerClass ：如果你不想让异常处理方法跟业务方法在同一个类中，可以使用 blockHandlerClass 为对应的类的 Class 对象，
     *       注意对应的函数必需为 static 函数，否则无法解析。
     *        底层原理：利用AOP切面来操作
     * @return
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

    @RestController
    public class TestController {

        private final RestTemplate restTemplate;

        @Autowired
        private ProviderClient providerClient;

        @Autowired
        public TestController(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

        /**
         * restTemplate方式调用----配置限流规则（资源名：/echo/{str}）
         * @param str
         * @return
         */
        @RequestMapping(value = "/echo/{str}", method = RequestMethod.GET)
        public String echo(@PathVariable String str) {
            return restTemplate.getForObject("http://service-provider/echo/" + str, String.class);
        }

        /**
         * （3）feign 方式调用----配置限流规则（资源名：/consumer/getTest）
         * Sentinel已经对做了整合，我们使用Feign的地方无需额外的注解。同时，@FeignClient注解中的所有属性，Sentinel都做了兼容。
         * 只需要在配置文件开启feign对sentinel的支持就可以了
         *
         * feign 使用限流功能，将限流规则配置请求路径资源上，返回Blocked by Sentinel (flow limiting)
         *        使用降级功能，将限流规则配置在feign调用的资源路径上 返回feign接口定义的降级回退处理类的信息
         * @param name
         * @return
         */
        @GetMapping("/consumer/getTest")
        public String getTest(@RequestParam("name") String name){
            return providerClient.test(name);
        }

    }
}
