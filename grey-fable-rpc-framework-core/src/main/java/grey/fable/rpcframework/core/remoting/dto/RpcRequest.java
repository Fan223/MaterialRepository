package grey.fable.rpcframework.core.remoting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求实体类.
 *
 * @author GreyFable
 * @since 2024/8/27 16:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    private String requestId;
    /**
     * 接口名.
     */
    private String interfaceName;

    /**
     * 方法名.
     */
    private String methodName;

    private Object[] parameters;

    private Class<?>[] paramTypes;

    private String group;

    private String version;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
