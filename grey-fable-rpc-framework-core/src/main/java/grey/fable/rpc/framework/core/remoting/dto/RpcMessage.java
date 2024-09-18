package grey.fable.rpc.framework.core.remoting.dto;

import lombok.*;

/**
 * RPC 信息.
 *
 * @author GreyFable
 * @since 2024/8/28 16:47
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * RPC 消息类型.
     */
    private byte messageType;

    /**
     * 序列化类型.
     */
    private byte codec;

    /**
     * 解压类型.
     */
    private byte compress;

    /**
     * 请求 ID.
     */
    private int requestId;

    /**
     * 消息数据, 请求/响应消息.
     */
    private Object data;
}
