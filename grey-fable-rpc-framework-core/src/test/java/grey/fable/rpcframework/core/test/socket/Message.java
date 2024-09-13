package grey.fable.rpcframework.core.test.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息实体类.
 *
 * @author GreyFable
 * @since 2024/8/22 8:52
 */
@Data
@AllArgsConstructor
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = -1858940911507663823L;

    private String content;
}
