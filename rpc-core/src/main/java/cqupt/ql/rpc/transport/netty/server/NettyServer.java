package cqupt.ql.rpc.transport.netty.server;

import cqupt.ql.rpc.AbstractRpcServer;
import cqupt.ql.rpc.codec.CommonDecoder;
import cqupt.ql.rpc.codec.CommonEncoder;
import cqupt.ql.rpc.enumeration.RpcError;
import cqupt.ql.rpc.exception.RpcException;
import cqupt.ql.rpc.hook.ShutdownHook;
import cqupt.ql.rpc.provider.ServiceProviderImpl;
import cqupt.ql.rpc.registry.NacosServiceRegistry;
import cqupt.ql.rpc.provider.ServiceProvider;
import cqupt.ql.rpc.registry.ServiceRegistry;
import cqupt.ql.rpc.serializer.CommonSerializer;
import cqupt.ql.rpc.serializer.KryoSerializer;
import cqupt.ql.rpc.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @className: NettyServer
 * @author: qinliang
 * @date: 2023/12/15
 **/
public class NettyServer extends AbstractRpcServer {
    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();  //自动扫描，然后注册服务，
    }



    @Override
    public void start() {
        //在RpcServer启动前，只需要调用addClearAllHook方法，就可以一个销毁服务的钩子，。
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new CommonEncoder(new JsonSerializer()));
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host,port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器是有错误发生：{}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
