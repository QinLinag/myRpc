package cqupt.ql.rpc.entity;

import cqupt.ql.rpc.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: RpcResponse
 * @author: qinliang
 * @date: 2023/12/15
 **/
@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    //响应对应的请求号
    private String requestId;

    //响应状态码
    private Integer statusCode;

    //响应补充信息
    private String message;

    //响应数据
    private T data;

    public static <T> RpcResponse<T> success(T data, String resquestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        response.setRequestId(resquestId);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code, String resquestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        response.setRequestId(resquestId);
        return response;
    }

}
