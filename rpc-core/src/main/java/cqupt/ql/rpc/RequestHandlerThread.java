//package cqupt.ql.rpc;
//
//import cqupt.ql.rpc.entity.RpcRequest;
//import cqupt.ql.rpc.handler.RequestHandler;
//import cqupt.ql.rpc.provider.ServiceProvider;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//
///**
// * @className: RequestHandlerThread
// * @author: qinliang
// * @date: 2023/12/15
// **/
//public class RequestHandlerThread implements Runnable{
//    private final static Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);
//
//    private Socket socket;
//    private RequestHandler requestHandler;
//    private ServiceProvider serviceProvider;
//
//    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceProvider serviceProvider) {
//        this.socket = socket;
//        this.requestHandler = requestHandler;
//        this.serviceProvider = serviceProvider;
//    }
//
//    @Override
//    public void run() {
//        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
//            String serviceName = rpcRequest.getInterfaceName();
//            Object service = serviceProvider.getServiceProvider(serviceName);
//            Object result = requestHandler.handle(rpcRequest, service);
//
//            objectOutputStream.writeObject(result);
//            objectOutputStream.flush();
//        } catch (IOException | ClassNotFoundException e) {
//            logger.error("调用或发送时有错误：" + e);
//        }
//    }
//}
