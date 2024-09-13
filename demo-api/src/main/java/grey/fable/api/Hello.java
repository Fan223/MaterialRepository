package grey.fable.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 测试实体类.
 *
 * @author GreyFable
 * @since 2024/8/29 15:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Hello implements Serializable {

    private String message;

    private String description;
}
