package cqupt.ql.rpc.provider;

import cqupt.ql.rpc.enumeration.RpcError;
import cqupt.ql.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: ServiceProviderImpl
 * @author: qinliang
 * @date: 2023/12/15
 **/
public class ServiceProviderImpl implements ServiceProvider {  //这是保存在本地的服务列表信息，
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private final static Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private final static Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized  <T> void addServiceProvider(T service, String serviceName) {
        //某个对象A实现了X和Y两个接口，  如果将A注册进来后，会有两个服务名X和Y对应于A对象。  这种处理方式也就说明了某个接口只能有一个对象提供服务
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口：{} 注册服务：{}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public synchronized Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
