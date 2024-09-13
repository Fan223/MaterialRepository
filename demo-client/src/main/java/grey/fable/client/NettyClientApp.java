package grey.fable.client;

import grey.fable.client.controller.HelloController;
import grey.fable.rpcframework.core.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 客户端启动类.
 *
 * @author GreyFable
 * @since 2024/8/29 15:46
 */
@RpcScan(basePackage = "grey.fable")
public class NettyClientApp {
    public static void main(String[] args) {
        // 创建基于注解的 Spring 应用上下文
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientApp.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
