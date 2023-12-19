package cqupt.ql.rpc.transport.netty.client;

import cqupt.ql.rpc.entity.RpcResponse;
import cqupt.ql.rpc.factory.SingletonFactory;
import cqupt.ql.rpc.transport.netty.server.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: NettyClientHandler
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {//只需要处理收到的消息，即RpcResponse对象，由于前面解码器解码了，这里直接将返回的结果放入ctx中即可
    private final static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final UnprocessedRequests unprocessedRequests;
    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try{
            logger.info(String.format("客户端接收到消息：%s", msg));
            unprocessedRequests.complete(msg);

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

}
