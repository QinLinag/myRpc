package cqupt.ql.rpc.exception;

import cqupt.ql.rpc.enumeration.RpcError;

/**
 * @className: RpcException
 * @author: qiliang
 * @date: 2023/12/15
 **/
public class RpcException extends RuntimeException {
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }
}
