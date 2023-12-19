package cqupt.ql.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @className: LoadBalancer
 * @author: Kevin
 * @date: 2023/12/16
 **/
public interface LoadBalancer {
    Instance select(List<Instance> instances);
}
