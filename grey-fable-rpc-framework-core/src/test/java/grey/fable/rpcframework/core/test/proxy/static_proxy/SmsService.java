package grey.fable.rpcframework.core.test.proxy.static_proxy;

/**
 * 短信接口
 *
 * @author Fan
 * @since 2023/5/5 11:28
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param message 消息
     * @return {@link String}
     * @author Fan
     * @since 2023/5/5 11:29
     */
    String send(String message);
}
