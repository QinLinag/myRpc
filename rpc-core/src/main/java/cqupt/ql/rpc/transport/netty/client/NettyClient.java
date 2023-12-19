package cqupt.ql.rpc.transport.netty.client;

import cqupt.ql.rpc.entity.RpcRequest;
import cqupt.ql.rpc.entity.RpcResponse;
import cqupt.ql.rpc.enumeration.RpcError;
import cqupt.ql.rpc.exception.RpcException;
import cqupt.ql.rpc.factory.SingletonFactory;
import cqupt.ql.rpc.loadbalancer.LoadBalancer;
import cqupt.ql.rpc.loadbalancer.RandomLoadBalancer;
import cqupt.ql.rpc.registry.NacosServiceDiscovery;
import cqupt.ql.rpc.registry.ServiceDiscovery;
import cqupt.ql.rpc.registry.ServiceRegistry;
import cqupt.ql.rpc.serializer.CommonSerializer;
import cqupt.ql.rpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @className: NettyClient
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class NettyClient implements RpcClient {
    private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final static EventLoopGroup group;

    private static final Bootstrap bootstrap;

    private CommonSerializer serializer;

    private ServiceRegistry serviceRegistry;

    private ServiceDiscovery serviceDiscovery;

    private final UnprocessedRequests unprocessedRequests;




    static{
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }


    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //向nacos服务器发送需要的服务名，获得服务提供方的地址，
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            //获得一个和服务提供端通信的channel,
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //将rpcRequest写给服务提供端，
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
//            channel.closeFuture().sync();
//            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
//            //获得服务提供方的调用返回结果，
//            RpcResponse rpcResponse = channel.attr(key).get();
//            return rpcResponse.getData();
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
