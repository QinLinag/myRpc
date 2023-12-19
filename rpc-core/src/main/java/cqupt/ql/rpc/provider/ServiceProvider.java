package cqupt.ql.rpc.provider;

/**
 * @className: ServiceProvider
 * @author: qinliang
 * @date: 2023/12/15
 **/
public interface ServiceProvider {
    <T> void addServiceProvider(T service, String serviceName);  //注册服务信息
    Object getServiceProvider(String serviceName);  //通过服务名，获得服务信息

}
