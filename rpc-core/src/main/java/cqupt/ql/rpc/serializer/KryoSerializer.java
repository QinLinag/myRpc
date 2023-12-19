package cqupt.ql.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import cqupt.ql.rpc.entity.RpcRequest;
import cqupt.ql.rpc.entity.RpcResponse;
import cqupt.ql.rpc.enumeration.SerializerCode;
import cqupt.ql.rpc.exception.SerializeException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @className: KryoSerializer
 * @author: qinliang
 * @date: 2023/12/16
 **/
public class KryoSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
       Kryo kryo = new Kryo();
       kryo.register(RpcResponse.class);
       kryo.register(RpcRequest.class);
       kryo.setReferences(true);
       return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream);) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch(Exception e) {
            logger.error("序列化时有错误：", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserializer(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch(Exception e) {
            logger.error("反序列化是有错误发生", e);
            throw new SerializeException("反序列化是有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
