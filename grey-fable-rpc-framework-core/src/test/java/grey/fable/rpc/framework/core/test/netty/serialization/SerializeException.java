package grey.fable.rpc.framework.core.test.netty.serialization;

import java.io.Serial;

/**
 * 序列化异常
 *
 * @author Fable
 * @since 2024/7/8 15:51
 */
public class SerializeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 60290963004345177L;

    public SerializeException(String message) {
        super(message);
    }
}