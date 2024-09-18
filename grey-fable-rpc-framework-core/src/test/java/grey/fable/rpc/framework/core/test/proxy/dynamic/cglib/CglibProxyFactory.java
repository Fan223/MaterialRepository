package grey.fable.rpc.framework.core.test.proxy.dynamic.cglib;

import net.sf.cglib.proxy.Enhancer;

/**
 * CGLIB 动态代理工厂类
 *
 * @author Fan
 * @since 2023/5/6 9:49
 */
public class CglibProxyFactory {

    private CglibProxyFactory() {
    }

    public static Object getProxy(Class<?> clazz) {
        // 创建动态代理增强类
        Enhancer enhancer = new Enhancer();
        // 设置类加载器
        enhancer.setClassLoader(clazz.getClassLoader());
        // 设置被代理类
        enhancer.setSuperclass(clazz);
        // 设置方法拦截器
        enhancer.setCallback(new DebugMethodInterceptor());
        // 创建代理类
        return enhancer.create();
    }
}
