package cqupt.ql.test;

import cqupt.ql.rpc.annotation.ServiceScan;
import cqupt.ql.rpc.api.HelloService;
import cqupt.ql.rpc.serializer.CommonSerializer;
import cqupt.ql.rpc.serializer.ProtobufSerializer;
import cqupt.ql.rpc.transport.netty.server.NettyServer;

/**
 * @className: NettyTestServer
 * @author: qinliang
 * @date: 2023/12/16
 **/
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
//        HelloService helloService = new HelloServiceImpl();
//        ServiceProvider registry = new ServiceProviderImpl();
//        registry.register(helloService);
//        NettyServer server = new NettyServer();
//        server.start(9999);


//        HelloService helloService = new HelloServiceImpl();
//        NettyServer server = new NettyServer("127.0.0.1", 9999);
//        server.setSerializer(new ProtobufSerializer());
//        server.publishService(helloService, HelloService.class);
        NettyServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
