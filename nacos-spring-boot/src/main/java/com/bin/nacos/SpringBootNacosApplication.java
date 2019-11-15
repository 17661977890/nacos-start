package com.bin.nacos;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @NacosPropertySource(dataId = "example", autoRefreshed = true)  加载nacos的配置源 dataid 为example，并开启自动更新
 */
@NacosPropertySource(dataId = "example", autoRefreshed = true)
@SpringBootApplication
public class SpringBootNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootNacosApplication.class, args);
    }

}
