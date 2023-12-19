package cqupt.ql.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @className: PackageType
 * @author: Kevin
 * @date: 2023/12/16
 **/
@AllArgsConstructor
@Getter
public enum PackageType {
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
