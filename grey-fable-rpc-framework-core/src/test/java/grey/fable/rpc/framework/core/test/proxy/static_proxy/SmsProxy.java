package grey.fable.rpc.framework.core.test.proxy.static_proxy;

import lombok.extern.slf4j.Slf4j;

/**
 * 短信代理类
 *
 * @author Fan
 * @since 2023/5/5 11:30
 */
@Slf4j
public class SmsProxy implements SmsService {

    private final SmsService smsService;

    public SmsProxy(SmsService smsService) {
        this.smsService = smsService;
    }

    @Override
    public String send(String message) {
        // 调用方法之前, 我们可以添加自己的操作
        log.info("before method send()");
        smsService.send(message);
        // 调用方法之后, 我们同样可以添加自己的操作
        log.info("after method send()");
        return null;
    }
}
