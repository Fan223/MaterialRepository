package grey.fable.rpc.framework.core.test.proxy.static_proxy;

/**
 * 静态代理测试类.
 *
 * @author Fan
 * @since 2023/5/5 11:35
 */
public class StaticProxyTest {
    public static void main(String[] args) {
        SmsService smsService = new SmsServiceImpl();
        SmsProxy smsProxy = new SmsProxy(smsService);
        smsProxy.send("hello");
    }
}
