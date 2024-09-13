package grey.fable.rpcframework.core.test.proxy.dynamic.cglib;

import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云短信实现类
 *
 * @author Fan
 * @since 2023/5/6 9:43
 */
@Slf4j
public class AliSmsService {

    public String send(String message) {
        log.info("Sending message: {}", message);
        return message;
    }
}
