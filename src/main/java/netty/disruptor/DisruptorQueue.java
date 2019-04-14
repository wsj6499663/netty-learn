package netty.disruptor;

import java.io.Closeable;

public interface DisruptorQueue<T> extends Closeable {
    void push(T t);
}
