package grey.fable.rpcframework.core.test.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Socket 客户端
 *
 * @author Fable
 * @since 2024/7/8 14:22
 */
@Slf4j
public class SocketClient {

    public Object send(Message message, String host, int port) {
        // 1. 创建 Socket 对象并且指定服务端的地址和端口号
        try (Socket socket = new Socket(host, port)) {
            // 2. 通过输出流向服务端发送请求信息
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            // 3. 通过输入流获取服务端响应的信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception: ", e);
        }
        return null;
    }

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        Message message = (Message) socketClient.send(new Message("来自客户端的消息"), "127.0.0.1", 6666);
        log.info("客户端接收到的消息: {}", message.getContent());
    }
}
