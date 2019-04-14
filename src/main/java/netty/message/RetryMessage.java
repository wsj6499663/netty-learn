package netty.message;

import lombok.Data;
import lombok.ToString;

import java.lang.ref.SoftReference;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@ToString
public class RetryMessage<T>  extends SoftReference<T> implements Delayed {
    private long timeOutTime;
    private long id;
    private T data;
    private int currentTimes = 0;
    private int defaultTimes = 5;
    private long timeWait=0;

    static long now() {
        return System.currentTimeMillis();
    }

    /**
     *
     * @param id
     * @param data
     * @param
     */
    public RetryMessage(long id, T data,long timeWait) {
        super(data);
        this.id = id;
        this.data = data;
        this.timeWait=timeWait;
        this.timeOutTime=now()+timeWait;
    }
    public void resetTimeOut(){
        this.timeOutTime=now()+this.timeWait;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.timeOutTime - now(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
