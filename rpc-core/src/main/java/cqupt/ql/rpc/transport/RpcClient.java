package cqupt.ql.rpc.transport;

import cqupt.ql.rpc.entity.RpcRequest;
import cqupt.ql.rpc.serializer.CommonSerializer;

/**
 * @className: RpcClient
 * @author: Kevin
 * @date: 2023/12/15
 **/
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
}
