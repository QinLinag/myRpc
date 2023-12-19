package cqupt.ql.rpc.transport.netty.server;

import cqupt.ql.rpc.entity.RpcRequest;
import cqupt.ql.rpc.entity.RpcResponse;
import cqupt.ql.rpc.handler.RequestHandler;
import cqupt.ql.rpc.provider.ServiceProviderImpl;
import cqupt.ql.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: NettyServerHandler
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {//NettyServerHandler 用于接收RpcRequest，并执行调用，将调用结果返回封装成RpcResponse发送出去
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceProvider serviceProvider;

    static{
        requestHandler = new RequestHandler();
        serviceProvider = new ServiceProviderImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try{
            if(msg.getHeartBeat()) {
                logger.info("接收到客户端心跳包...");
                return;
            }
            logger.info("服务器接收到请求：{}", msg);
            Object result = requestHandler.handle(msg);
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                //服务提供方，将处理结果response写回给客户端，
                ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            } else {
                logger.error("通道不可写");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误:");
        cause.printStackTrace();
        ctx.close();
    }
}
