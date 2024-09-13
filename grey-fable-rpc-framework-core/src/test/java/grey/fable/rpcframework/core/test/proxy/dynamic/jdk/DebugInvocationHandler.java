package grey.fable.rpcframework.core.test.proxy.dynamic.jdk;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JDK 动态代理类
 *
 * @author Fan
 * @since 2023/5/6 9:02
 */
@Slf4j
public class DebugInvocationHandler implements InvocationHandler {

    /**
     * 代理类中的真实对象
     */
    private final Object target;

    public DebugInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 调用方法之前，我们可以添加自己的操作
        log.info("before method " + method.getName());
        // 调用真实对象的方法
        Object result = method.invoke(target, args);
        //调用方法之后，我们同样可以添加自己的操作
        log.info("after method " + method.getName());
        return result;
    }
}
