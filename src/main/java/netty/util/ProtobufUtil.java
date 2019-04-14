package netty.util;

import com.google.common.base.Predicates;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;

public class ProtobufUtil {
    private static final ConcurrentHashMap<String, Schema> schemas = new ConcurrentHashMap<>();
    public static <T> T deserialize(byte[] body, Class<T> cls) {
        T t = null;
        try {
            t = cls.newInstance();
            Schema schema = RuntimeSchema.getSchema(cls);
            ProtostuffIOUtil.mergeFrom(body, t, schema);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Predicates.isNull().apply(t);
        return t;
    }

    public static <T> byte[] serializer(T o) {
        Schema schema = getSchema(o);
        return ProtostuffIOUtil.toByteArray(o, schema, LinkedBuffer.allocate(1024));
    }
    private static <T> Schema getSchema(T o){
        String className = getClassName(o);
        if (schemas.containsKey(className)) {
            return schemas.get(className);
        }
        return createSchema(o);
    }

    private static <T>  String getClassName(T o) {
        return o.getClass().getName();
    }

    private static synchronized <T> Schema createSchema(T o){
        String className = getClassName(o);
        if (schemas.containsKey(className)) {
            return schemas.get(className);
        }
        Schema schema = RuntimeSchema.getSchema(o.getClass());
        schemas.put(className, schema);
        return schema;
    }
}
