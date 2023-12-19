//package cqupt.ql.rpc.transport;
//
//import cqupt.ql.rpc.RequestHandlerThread;
//import cqupt.ql.rpc.entity.RpcRequest;
//import cqupt.ql.rpc.entity.RpcResponse;
//import cqupt.ql.rpc.handler.RequestHandler;
//import cqupt.ql.rpc.provider.ServiceProvider;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.*;
//
///**
// * @className: RpcServer
// * @author: qinliang
// * @date: 2023/12/15
// **/
//public class RpcServer1 {       //Dubbo第 一二章实现的，
//    private static final Logger logger = LoggerFactory.getLogger(RpcServer1.class);
//    private final ExecutorService threadPool;
//
//    private RequestHandler requestHandler = new RequestHandler();
//
//    private final ServiceProvider serviceProvider;
//
//    public RpcServer1(ServiceProvider serviceProvider) {
//        this.serviceProvider = serviceProvider;
//        int corePoolSize = 5;
//        int maximumPoolSize = 50;
//        long keepAliveTime = 60;
//        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);  //默认等待队列中最大任务个数100
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
//    }
//
//
//    public void register(Object service) {
//        //serviceProvider.register(service);
//    }
//
//
//
//
//    //服务端，使用socket循环监听某个端口，
//    public void start(int port) {
//        try(ServerSocket serverSocket = new ServerSocket(port)) {
//            logger.info("服务器正在启动...");
//            Socket socket;
//            while((socket = serverSocket.accept()) != null) {
//                logger.info("客户端连接成功！IP为：" + socket.getInetAddress());
//                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceProvider));
//            }
//        } catch(IOException e) {
//            logger.error("连接是错误：" + e);
//        }
//    }
//
//}
//
//class WorkerThread implements Runnable{
//    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);
//    Object service;
//    Socket socket;
//
//    public WorkerThread(Socket socket, Object service) {
//        this.socket = socket;
//        this.service = service;
//    }
//
//    @Override
//    public void run() {
//        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
//            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
//            Object returnObject = method.invoke(service, rpcRequest.getParameters());
//            //封装一个rpcResponse返回给调用者，
//            RpcResponse<Object> rpcResponse = RpcResponse.success(returnObject);
//            objectOutputStream.flush();
//            objectOutputStream.writeObject(rpcResponse);
//        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            logger.error("调用或发送时出现错误" + e);
//        }
//    }
//}
//
