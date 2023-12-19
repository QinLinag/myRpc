package cqupt.ql.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @className: RoundRobinLoadBalancer
 * @author: Kevin
 * @date: 2023/12/16
 **/
public class RoundRobinLoadBalancer implements LoadBalancer{  //轮询算法
    private int index = 0;
    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
