package grey.fable.rpcframework.core.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import grey.fable.rpcframework.core.remoting.dto.RpcRequest;
import grey.fable.rpcframework.core.remoting.dto.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo 序列化实现类.
 *
 * @author Fable
 * @since 2024/7/8 15:45
 */
public class KryoSerialization implements Serialization {

    /**
     * 由于 Kryo 不是线程安全的, 每个线程都应该有自己的 Kryo Input 和 Output 实例, 所以使用 ThreadLocal 存放 Kryo 对象.
     */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        // 默认值为 true, 是否关闭注册行为, 关闭之后可能存在序列化问题, 一般推荐设置为 true
        kryo.setReferences(true);
        // 默认值为 false, 是否关闭循环引用, 可以提高性能, 但是一般不推荐设置为 true
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            // 获取 Kryo 对象
            Kryo kryo = kryoThreadLocal.get();
            // Object -> byte, 将对象序列化为 byte 数组.
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializationException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            // byte -> Object, 从 byte 数组中反序列化出对象.
            Object obj = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new SerializationException("反序列化失败");
        }
    }
}
