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
@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl 被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl 收到消息: {}.", hello.getMessage());
        String result = "消息描述为: " + hello.getDescription();
        log.info("HelloServiceImpl 返回消息: {}.", result);
        return result;
    }
}
