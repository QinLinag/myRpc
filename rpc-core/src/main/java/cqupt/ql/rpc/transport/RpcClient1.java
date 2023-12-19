package cqupt.ql.rpc.transport;

import cqupt.ql.rpc.entity.RpcRequest;
import cqupt.ql.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * @className: RpcClient1
 * @author: qinliang
 * @date: 2023/12/15
 **/
public class RpcClient1 {    //发送rpcRequest，并且接收object结果       Dubbo第一章实现的，
    private static final Logger logger = LoggerFactory.getLogger(RpcClient1.class);

    public Object sendRequest(RpcRequest request, String host, int port) {
        try(Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            return ((RpcResponse)objectInputStream.readObject());  //返回结果
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时发生错误：" + e);
            return null;
        }
    }
}
