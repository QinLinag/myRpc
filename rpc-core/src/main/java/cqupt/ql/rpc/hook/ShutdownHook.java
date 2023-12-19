package cqupt.ql.rpc.hook;

import cqupt.ql.rpc.factory.ThreadPoolFactory;
import cqupt.ql.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @className: ShutdownHook
 * @author: Kevin
 * @date: 2023/12/16
 **/
public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");

    private static final ShutdownHook shutdownHook = new ShutdownHook();  //单例模式，懒汉式


    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后自动注销所有服务");
        //RunTime对象是JVM虚拟机运行时环境，调用其addShutdownHook方法增加一个钩子函数，创建一个新线程调用clearRegistry方法完成注销。这个钩子函数会在JVM关闭前被调用
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();;
            threadPool.shutdown();
        }));
    }



}
