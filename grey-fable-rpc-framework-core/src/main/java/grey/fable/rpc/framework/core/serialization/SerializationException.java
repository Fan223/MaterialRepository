package grey.fable.rpc.framework.core.serialization;

import java.io.Serial;

/**
 * 序列化异常
 *
 * @author Fable
 * @since 2024/7/8 15:51
 */
public class SerializationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 60290963004345177L;

    public SerializationException(String message) {
        super(message);
    }
}
