package grey.fable.rpc.framework.core.remoting.transport.netty.server;

import grey.fable.base.factory.SingletonFactory;
import grey.fable.rpc.framework.core.enums.CompressTypeEnum;
import grey.fable.rpc.framework.core.enums.SerializationTypeEnum;
import grey.fable.rpc.framework.core.remoting.constant.RpcConstants;
import grey.fable.rpc.framework.core.remoting.dto.RpcMessage;
import grey.fable.rpc.framework.core.remoting.dto.RpcRequest;
import grey.fable.rpc.framework.core.remoting.dto.RpcResponse;
import grey.fable.rpc.framework.core.remoting.hander.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端处理器, 接收客户端发送过来的消息并返回结果给客户端.
 *
 * @author GreyFable
 * @since 2024/8/29 15:00
 */
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage rpcMessage) {
                log.info("服务器收到消息: [{}] ", msg);
                // 获取消息类型
                byte messageType = rpcMessage.getMessageType();
                // 设置序列化方式
                rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                // 设置压缩方式
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                // 处理心跳请求, 返回心跳响应.
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    // 处理 RPC 请求
                    RpcRequest rpcRequest = (RpcRequest) rpcMessage.getData();
                    // 执行目标方法(客户端需要执行的方法), 并返回方法结果.
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("服务器获取结果: %s", result.toString()));
                    // 设置响应消息类型
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    // 设置响应对象
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(500, "远程调用失败!");
                        rpcMessage.setData(rpcResponse);
                        log.error("现在不可写，消息已丢弃");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            // 确保释放 ByteBuf, 否则可能存在内存泄漏
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idlestateevent) {
            IdleState state = idlestateevent.state();
            if (state == IdleState.READER_IDLE) {
                log.info("空闲检查发生, 关闭连接");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务器捕获异常");
        cause.printStackTrace();
        ctx.close();
    }
}
