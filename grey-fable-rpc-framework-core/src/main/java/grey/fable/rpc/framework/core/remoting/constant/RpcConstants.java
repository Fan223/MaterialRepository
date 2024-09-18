package grey.fable.rpc.framework.core.remoting.constant;

/**
 * RPC 常量.
 *
 * @author GreyFable
 * @since 2024/8/28 16:55
 */
public class RpcConstants {

    /**
     * 消息头长度.
     */
    public static final int HEAD_LENGTH = 16;

    public static final byte TOTAL_LENGTH = 16;

    /**
     * 魔数, 验证 RpcMessage.
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};

    /**
     * 版本号.
     */
    public static final byte VERSION = 1;

    /**
     * 请求类型.
     */
    public static final byte REQUEST_TYPE = 1;

    /**
     * 响应类型.
     */
    public static final byte RESPONSE_TYPE = 2;

    /**
     * ping, 心跳请求类型.
     */
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;

    /**
     * pong, 心跳响应类型.
     */
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    public static final String PING = "ping";

    public static final String PONG = "pong";

    /**
     * 最大数据帧长度.
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    private RpcConstants() {
    }
}
