package com.alibaba.nacos.example.spring.cloud;

import org.springframework.stereotype.Component;

/**
 * feign 服务降级处理类   注意使用服务降级时，要将限流规则配置在feign调用的资源路径上，而不是请求路径
 * @author bin
 * @Date
 */
@Component
public class FeignFallBack implements ProviderClient{

    @Override
    public String test(String name) {
        System.out.println("==========================================");
        return "Feign FallBack Msg";
    }
}
