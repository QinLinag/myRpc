package cqupt.ql.rpc.transport.socket.server;

import cqupt.ql.rpc.AbstractRpcServer;
import cqupt.ql.rpc.factory.ThreadPoolFactory;
import cqupt.ql.rpc.handler.RequestHandler;
import cqupt.ql.rpc.hook.ShutdownHook;
import cqupt.ql.rpc.provider.ServiceProviderImpl;
import cqupt.ql.rpc.registry.NacosServiceRegistry;
import cqupt.ql.rpc.serializer.CommonSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @className: SocketServer
 * @author: Kevin
 * @date: 2023/12/17
 **/
public class SocketServer extends AbstractRpcServer {
    private final ExecutorService threadPool;

    private final CommonSerializer serializer;

    private final RequestHandler requestHandler = new RequestHandler();

    private SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    private SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }


    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()){
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("rpc服务器接收到请求：{}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误");
        }
    }
}
