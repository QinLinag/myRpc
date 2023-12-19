package cqupt.ql.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @className: RandomLoadBalancer
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class RandomLoadBalancer implements LoadBalancer{   //随机算法
    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
