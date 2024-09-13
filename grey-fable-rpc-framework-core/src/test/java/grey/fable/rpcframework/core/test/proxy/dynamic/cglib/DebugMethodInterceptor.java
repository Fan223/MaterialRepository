package grey.fable.rpcframework.core.test.proxy.dynamic.cglib;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB 动态代理拦截器
 *
 * @author Fan
 * @since 2023/5/6 9:44
 */
@Slf4j
public class DebugMethodInterceptor implements MethodInterceptor {

    /**
     * 拦截方法执行
     *
     * @param obj    代理对象(增强的对象)
     * @param method 被拦截的方法(需要增强的方法)
     * @param args   方法入参
     * @param proxy  用于调用原始方法
     * @return {@link Object}
     * @author Fan
     * @since 2023/5/6 9:45
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 调用方法之前, 我们可以添加自己的操作
        log.info("before method " + method.getName());
        // 执行原始方法
        Object object = proxy.invokeSuper(obj, args);
        // 调用方法之后, 我们同样可以添加自己的操作
        log.info("after method " + method.getName());
        return object;
    }
}
