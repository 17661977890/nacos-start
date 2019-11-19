package com.alibaba.nacos.example.spring.cloud;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * feign 接口 定义服务降级回退类
 */
@FeignClient(name = "service-provider",fallback = FeignFallBack.class)
public interface ProviderClient {
    @GetMapping("/test")
    String test(@RequestParam("name") String name);
}
