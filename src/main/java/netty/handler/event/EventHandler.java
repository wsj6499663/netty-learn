package netty.handler.event;

public interface EventHandler<T> {
    void handler(T var);
    String type();
}
