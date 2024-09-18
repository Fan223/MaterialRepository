package grey.fable.rpc.framework.core.remoting.transport;

import grey.fable.rpc.framework.core.extension.SPI;
import grey.fable.rpc.framework.core.remoting.dto.RpcRequest;

/**
 * 传输请求接口.
 *
 * @author GreyFable
 * @since 2024/8/28 9:56
 */
@SPI
public interface RpcRequestTransport {

    /**
     * 向服务器发送 RPC 请求并获取结果.
     *
     * @param rpcRequest {@link RpcRequest}
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/8/28 9:57
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
