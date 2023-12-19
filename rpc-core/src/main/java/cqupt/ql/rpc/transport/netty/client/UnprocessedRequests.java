package cqupt.ql.rpc.transport.netty.client;

import cqupt.ql.rpc.entity.RpcRequest;
import cqupt.ql.rpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: UnprocessedRequests
 * @author: qinliang
 * @date: 2023/12/17
 **/
public class UnprocessedRequests {  //Netty客户端使用CompletableFuture接收返回结果，优化Netty客户端连接逻辑
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }


}
