package grey.fable.rpcframework.core.remoting.hander;

import grey.fable.base.factory.SingletonFactory;
import grey.fable.rpcframework.core.exception.RpcException;
import grey.fable.rpcframework.core.provider.ServiceProvider;
import grey.fable.rpcframework.core.provider.impl.ZkServiceProviderImpl;
import grey.fable.rpcframework.core.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RPC 请求处理器.
 *
 * @author GreyFable
 * @since 2024/8/29 9:49
 */
@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 处理 rpcRequest: 调用相应的方法，然后返回该方法
     *
     * @param rpcRequest {@link RpcRequest}
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/8/29 10:03
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 获取方法执行结果
     *
     * @param rpcRequest client request
     * @param service    service object
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/8/29 10:03
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("服务:[{}] 成功调用方法:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
