package grey.fable.rpcframework.core.test.netty.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户端请求实体类
 *
 * @author Fable
 * @since 2024/7/8 14:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    private String interfaceName;

    private String methodName;
}