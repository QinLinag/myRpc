package cqupt.ql.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import cqupt.ql.rpc.enumeration.RpcError;
import cqupt.ql.rpc.exception.RpcException;
import cqupt.ql.rpc.loadbalancer.LoadBalancer;
import cqupt.ql.rpc.loadbalancer.RandomLoadBalancer;
import cqupt.ql.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @className: NacosServiceDiscovery
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class NacosServiceDiscovery implements ServiceDiscovery{
    private final static Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    //将负载均衡集成到服务发现里面，
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            this.loadBalancer = new RandomLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if (instances.size() == 0) {
                logger.error("找不到对应的服务：" + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);    //这里涉及到负载均衡，
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时发生错误：", e);
        }
        return null;
    }
}
