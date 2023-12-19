package cqupt.ql.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @className: ServiceRegistry
 * @author: qinliang
 * @date: 2023/12/16
 **/
public interface ServiceRegistry {
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
