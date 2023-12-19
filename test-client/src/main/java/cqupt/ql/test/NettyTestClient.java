package cqupt.ql.test;

import cqupt.ql.rpc.api.ByeService;
import cqupt.ql.rpc.api.HelloObject;
import cqupt.ql.rpc.api.HelloService;
import cqupt.ql.rpc.serializer.CommonSerializer;
import cqupt.ql.rpc.serializer.ProtobufSerializer;
import cqupt.ql.rpc.transport.RpcClient;
import cqupt.ql.rpc.transport.RpcClientProxy;
import cqupt.ql.rpc.transport.netty.client.NettyClient;

/**
 * @className: NettyTestClient
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class NettyTestClient {
    public static void main(String[] args) {
//        RpcClient client = new NettyClient("127.0.0.1", 9999);
//        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
//        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
//        HelloObject object = new HelloObject(12, "this is a message");
//        String res = (String) helloService.hello(object);
//        System.out.println(res);
//        RpcClient client = new NettyClient();
//        client.setSerializer(new ProtobufSerializer());
//        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
//        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
//        HelloObject object = new HelloObject(12, "this is a message");
//        String res = (String) helloService.hello(object);
//        System.out.println(res);

        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        HelloObject object = new HelloObject(12, "this is message");
        String res = helloService.hello(object);
        System.out.println(res);

        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("aniya"));

    }
}
