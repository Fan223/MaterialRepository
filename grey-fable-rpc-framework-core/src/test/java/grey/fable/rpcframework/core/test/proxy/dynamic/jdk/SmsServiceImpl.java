package grey.fable.rpcframework.core.test.proxy.dynamic.jdk;

import lombok.extern.slf4j.Slf4j;

/**
 * 短信接口实现类
 *
 * @author Fan
 * @since 2023/5/5 11:29
 */
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Override
    public String send(String message) {
        log.info("Sending message: {}", message);
        return message;
    }
}
