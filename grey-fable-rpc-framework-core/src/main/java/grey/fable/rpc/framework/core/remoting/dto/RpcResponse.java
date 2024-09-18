package grey.fable.rpc.framework.core.remoting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应实体类.
 *
 * @author GreyFable
 * @since 2024/8/27 16:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {

    private String requestId;

    private Integer code;

    private String message;

    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(200);
        response.setMessage("远程调用成功");
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    public static <T> RpcResponse<T> fail(Integer code, String message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
