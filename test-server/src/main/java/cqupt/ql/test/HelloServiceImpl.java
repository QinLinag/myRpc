package cqupt.ql.test;

import cqupt.ql.rpc.annotation.Service;
import cqupt.ql.rpc.api.HelloObject;
import cqupt.ql.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @className: HelloServiceImpl
 * @author: qinliang
 * @date: 2023/12/15
 **/
@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}", object.getMessage());
        return "这是调用的返回值，id=" + object.getId();
    }
}
