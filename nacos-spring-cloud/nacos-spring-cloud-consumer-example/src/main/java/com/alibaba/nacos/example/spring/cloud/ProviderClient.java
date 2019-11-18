package com.alibaba.nacos.example.spring.cloud;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-provider")
public interface ProviderClient {

    @GetMapping("/test")
    String test(@RequestParam("name") String name);
}
