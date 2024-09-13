package grey.fable.rpcframework.core.config;

import lombok.*;

/**
 * RPC 服务相关属性.
 *
 * @author GreyFable
 * @since 2024/8/29 9:53
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    /**
     * 当接口有多个实现类时, 按组区分.
     */
    private String group = "";

    /**
     * 版本号.
     */
    private String version = "";

    /**
     * 目标服务.
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
