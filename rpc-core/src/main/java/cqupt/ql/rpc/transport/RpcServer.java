package cqupt.ql.rpc.transport;

import cqupt.ql.rpc.serializer.CommonSerializer;

/**
 * @className: RpcServer
 * @author: qinliang
 * @date: 2023/12/15
 **/
public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start();

    <T> void publishService(T service, String serviceName);

}
