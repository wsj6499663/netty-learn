package netty.handler;

public interface EventHandler<T> {
    void handler(T var);
    String type();
}
