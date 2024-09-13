package grey.fable.server.service.impl;

import grey.fable.api.Hello;
import grey.fable.api.HelloService;
import grey.fable.rpcframework.core.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试类
 *
 * @author GreyFable
 * @since 2024/8/29 15:40
 */
@Slf4j
@RpcService(group = "test2", version = "version2")
public class HelloServiceImpl2 implements HelloService {

    static {
        System.out.println("HelloServiceImpl2 被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2 收到消息: {}.", hello.getMessage());
        String result = "消息描述为: " + hello.getDescription();
        log.info("HelloServiceImpl2 返回消息: {}.", result);
        return result;
    }
}
