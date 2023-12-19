package cqupt.ql.rpc;

import cqupt.ql.rpc.annotation.Service;
import cqupt.ql.rpc.annotation.ServiceScan;
import cqupt.ql.rpc.enumeration.RpcError;
import cqupt.ql.rpc.exception.RpcException;
import cqupt.ql.rpc.provider.ServiceProvider;
import cqupt.ql.rpc.registry.ServiceRegistry;
import cqupt.ql.rpc.transport.RpcServer;
import cqupt.ql.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @className: AbstractRpcServer
 * @author: qinliang
 * @date: 2023/12/17
 **/
public abstract class AbstractRpcServer implements RpcServer {  //因为注解自动注册服务，netty和socket两种都需要用到。所以抽象一个类abstractRpcServer继承RpcServer
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {

            if(clazz.isAnnotationPresent(Service.class)) {
                System.out.println(clazz.getCanonicalName());
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        System.out.println(oneInterface.getCanonicalName());
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
