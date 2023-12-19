package cqupt.ql.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @className: ServiceDiscovery
 * @author: qinliang
 * @date: 2023/12/16
 **/
public interface ServiceDiscovery {
    InetSocketAddress lookupService(String serviceName);
}
