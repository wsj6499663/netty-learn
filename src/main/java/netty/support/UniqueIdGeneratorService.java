package netty.support;

public interface UniqueIdGeneratorService {
    long nextId();
    void init();
}
