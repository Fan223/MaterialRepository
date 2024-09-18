package grey.fable.rpc.framework.core.test.proxy.dynamic.jdk;

/**
 * JDK 动态代理测试类
 *
 * @author Fan
 * @since 2023/5/6 9:11
 */
public class JdkProxyTest {
    public static void main(String[] args) {
        SmsService smsService = (SmsService) JdkProxyFactory.getProxy(new SmsServiceImpl());
        smsService.send("Hello");
    }
}
