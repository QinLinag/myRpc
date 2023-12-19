package cqupt.ql.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import cqupt.ql.rpc.enumeration.RpcError;
import cqupt.ql.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @className: NacosUtil
 * @author: Kevin
 * @date: 2023/12/16
 **/
public class NacosUtil {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;

    private static final Set<String> serviceNames = new HashSet<>();

    private static InetSocketAddress address;

    private static final String SERVER_ADDR = "127.0.0.1:8848";  //本来nacos的地址信息是可以配置的，但是这里定死了，

    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService() {
        try{
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch(NacosException e) {
            logger.error("连接到nacos时发生了错误：", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        serviceNames.add(serviceName); //记录一下这个server注册到nacos的服务名，方便后面使用，
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                try{
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.error("注销服务{} 失败：", serviceName, e);
                }
            }
        }
    }
}
