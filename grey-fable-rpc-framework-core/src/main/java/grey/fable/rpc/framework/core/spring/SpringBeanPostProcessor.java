package grey.fable.rpc.framework.core.spring;

import grey.fable.base.factory.SingletonFactory;
import grey.fable.rpc.framework.core.extension.ExtensionLoader;
import grey.fable.rpc.framework.core.annotation.RpcReference;
import grey.fable.rpc.framework.core.annotation.RpcService;
import grey.fable.rpc.framework.core.config.RpcServiceConfig;
import grey.fable.rpc.framework.core.provider.ServiceProvider;
import grey.fable.rpc.framework.core.provider.impl.ZkServiceProviderImpl;
import grey.fable.rpc.framework.core.proxy.RpcClientProxy;
import grey.fable.rpc.framework.core.remoting.transport.RpcRequestTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Bean 处理器.
 *
 * @author GreyFable
 * @since 2024/8/29 15:23
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    /**
     * Bean 初始化前调用, 将带有 RpcService 注解的 Bean 注册到注册中心.
     *
     * @param bean
     * @param beanName
     * @return {@link java.lang.Object}
     * @author GreyFable
     * @since 2024/9/11 16:22
     */
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // 获取 RpcService 注解
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // 构建 RpcService Properties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    /**
     * Bean 初始化后, 检查属性有没有 RpcReference 注解, 如果有的话, 给该属性设置值为代理类, 代理类的代理方法即发送请求给服务端获取数据.
     *
     * @param bean
     * @param beanName
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/9/11 15:39
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
