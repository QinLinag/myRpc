package cqupt.ql.rpc.transport.socket.server;

import cqupt.ql.rpc.entity.RpcRequest;
import cqupt.ql.rpc.entity.RpcResponse;
import cqupt.ql.rpc.handler.RequestHandler;
import cqupt.ql.rpc.serializer.CommonSerializer;
import cqupt.ql.rpc.transport.netty.server.util.ObjectReader;
import cqupt.ql.rpc.transport.netty.server.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @className: SocketRequestHandlerThread
 * @author: Kevin
 * @date: 2023/12/17
 **/
public class SocketRequestHandlerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);
    private Socket socket;

    private RequestHandler requestHandler;

    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }


    @Override
    public void run() {
        try(InputStream inputStream = socket.getInputStream()) {
            OutputStream outputStream = socket.getOutputStream();
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误:", e);
        }
    }
}
