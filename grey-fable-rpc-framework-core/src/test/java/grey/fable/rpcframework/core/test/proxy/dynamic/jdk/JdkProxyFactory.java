package grey.fable.rpcframework.core.test.proxy.dynamic.jdk;

import java.lang.reflect.Proxy;

/**
 * 代理对象工厂类
 *
 * @author Fan
 * @since 2023/5/6 9:08
 */
public class JdkProxyFactory {

    private JdkProxyFactory() {
    }

    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                // 目标类的类加载器
                target.getClass().getClassLoader(),
                // 代理需要实现的接口, 可指定多个
                target.getClass().getInterfaces(),
                // 代理对象对应的自定义 InvocationHandler
                new DebugInvocationHandler(target)
        );
    }
}
