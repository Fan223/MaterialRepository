package grey.fable.rpcframework.core.test.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket 服务端
 *
 * @author Fable
 * @since 2024/7/8 14:11
 */
@Slf4j
public class SocketServer {

    public void start(int port) {
        // 1. 创建 ServerSocket 对象并且绑定一个端口
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;

            // 2. 通过 accept() 方法监听客户端请求
            while (null != (socket = serverSocket.accept())) {
                log.info("客户端连接成功");
                // 3. 通过输入流读取客户端发送的请求信息
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                log.info("服务端接收到的消息: {}", message.getContent());
                // 4. 通过输出流向客户端发送响应信息
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                message.setContent("服务端响应消息");
                oos.writeObject(message);
                oos.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur IOException:", e);
        }
    }

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        socketServer.start(6666);
    }
}
