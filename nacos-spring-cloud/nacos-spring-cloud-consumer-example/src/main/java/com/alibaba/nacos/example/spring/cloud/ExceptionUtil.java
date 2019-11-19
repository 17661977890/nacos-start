package com.alibaba.nacos.example.spring.cloud;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.alibaba.sentinel.rest.SentinelClientHttpResponse;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.stereotype.Component;

/**
 * restTemplate 服务降级处理类-----定义限流规则在需调用的资源路径
 *
 * 注意处理类的方法不能随便写，方法参数是固定的那四个，而且规定了返回值
 *
 * java.lang.IllegalStateException: Cannot find method [handleException] in class
 * [com.alibaba.nacos.example.spring.cloud.ExceptionUtil]
 * with parameters  方法参数
 * --->interface org.springframework.http.HttpRequest,
 * --->class [B,
 * --->interface org.springframework.http.client.ClientHttpRequestExecution,
 * --->class com.alibaba.csp.sentinel.slots.block.BlockException
 * --->is not ClientHttpResponse 方法返回值
 * in flow control
 * @author bin
 * @Date
 */
@Slf4j
public class ExceptionUtil {

//    public static String handleException(BlockException ex) {
//        return "扛不住了啊....";
//    }

    public static SentinelClientHttpResponse handleException(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        log.error(ex.getMessage(), ex);
        return new SentinelClientHttpResponse("RestTemplate FallBack Msg");
    }
}
