package cqupt.ql.rpc.transport.netty.client;

import cqupt.ql.rpc.codec.CommonDecoder;
import cqupt.ql.rpc.codec.CommonEncoder;
import cqupt.ql.rpc.serializer.CommonSerializer;
import cqupt.ql.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @className: ChannelProvider
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static EventLoopGroup eventLoopGroup;

    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channels != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //自定义序列化编解码器
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch(ExecutionException e) {
            logger.error("连接客户端时有错误发生，", e);
            return null;
        }
        return channel;
    }


    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
           if (future.isSuccess()) {
               logger.info("客户端连接成功！");
               completableFuture.complete(future.channel());
           } else {
               throw new IllegalStateException();
           }
        });
        return completableFuture.get();
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接时间超时，如果时间到了还是连接不上，代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启tcp底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了Nagle算法，该算法作用是尽可能发送大数据块，减少网络传输。TCP_NODELAY参数作用就是控制是否启用Nagle算法
                .option(ChannelOption.TCP_NODELAY, true);

        return bootstrap;
    }


}
