package grey.fable.rpcframework.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 解压枚举.
 *
 * @author GreyFable
 * @since 2024/8/28 16:53
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
