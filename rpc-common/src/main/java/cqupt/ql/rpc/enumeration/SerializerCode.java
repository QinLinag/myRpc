package cqupt.ql.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @className: SerializerCode
 * @author: qinliang
 * @date: 2023/12/16
 **/
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;
}
