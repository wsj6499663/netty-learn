package netty.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RetryMessage<T> {
    private long startTime = now() + 10000L;
    private long id;
    private T data;
    private int currentTimes = 1;
    private int defaultTimes = 5;

    static long now() {
        return System.currentTimeMillis();
    }

    public RetryMessage(long id, T data) {
        this.id = id;
        this.data = data;
    }
}
