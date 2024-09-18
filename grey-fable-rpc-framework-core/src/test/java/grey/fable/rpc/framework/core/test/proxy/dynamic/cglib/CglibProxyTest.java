package grey.fable.rpc.framework.core.test.proxy.dynamic.cglib;

/**
 * CGLIB 动态代理测试类
 *
 * @author Fan
 * @since 2023/5/6 9:49
 */
public class CglibProxyTest {
    public static void main(String[] args) {
        AliSmsService aliSmsService = (AliSmsService) CglibProxyFactory.getProxy(AliSmsService.class);
        aliSmsService.send("Hello");
    }
}
