package grey.fable.client.controller;

import grey.fable.api.Hello;
import grey.fable.api.HelloService;
import grey.fable.rpcframework.core.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * 测试控制器.
 *
 * @author GreyFable
 * @since 2024/8/29 15:47
 */
@Component
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    public void test() {
        this.helloService.hello(new Hello("客户端消息, version1", "test1"));
    }
}
