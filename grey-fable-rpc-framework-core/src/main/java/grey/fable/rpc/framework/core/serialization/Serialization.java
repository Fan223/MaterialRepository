package grey.fable.rpc.framework.core.serialization;

import grey.fable.rpc.framework.core.extension.SPI;

/**
 * 序列化接口
 *
 * @author Fable
 * @since 2024/7/8 15:44
 */
@SPI
public interface Serialization {

    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return {@link byte[]}
     * @author Fable
     * @since 2024/7/8 15:44
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 类
     * @return {@link T}
     * @author Fable
     * @since 2024/7/8 15:45
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}