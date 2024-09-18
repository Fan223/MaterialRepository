package grey.fable.rpc.framework.core.test.netty.server;

import lombok.*;

/**
 * 服务端响应实体类
 *
 * @author Fable
 * @since 2024/7/8 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RpcResponse {

    private String message;
}