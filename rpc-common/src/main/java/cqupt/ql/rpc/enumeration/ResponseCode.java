package cqupt.ql.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @className: ResponseCode
 * @author: Kevin
 * @date: 2023/12/15
 **/
@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200, "调用方法成功"),
    FAIL(500, "调用方法失败"),
    METHOD_NOT_FOUND(500, "未找到指定方法"),
    CLASS_NOT_FOUND(500, "未找到指定类");

    private final int code;
    private final String message;

}
