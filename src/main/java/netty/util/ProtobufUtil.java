package netty.util;

import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtobufUtil {
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
        assert t != null;
        return t;
    }
}
