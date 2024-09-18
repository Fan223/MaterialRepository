package grey.fable.rpc.framework.core.exception;

import grey.fable.rpc.framework.core.enums.RpcErrorMessageEnum;

/**
 * RPC 异常.
 *
 * @author GreyFable
 * @since 2024/8/28 14:19
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
