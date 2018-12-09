package netty.support;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import netty.config.ConfigManager;
import netty.message.RetryMessage;
import netty.util.ThreadUtil;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class RetryMessageQueue<T> {
    private volatile boolean isClosed = false;
    private Map<Long, SoftReference<RetryMessage<T>>> map = Maps.newHashMap();
    private Function<RetryMessage<T>, Object> callback;
    private int maxQueueSize;
    private volatile int currentSize;

    public RetryMessageQueue(Function<RetryMessage<T>, Object> callback) {
        this.callback = callback;
    }

    public void init() {
        this.maxQueueSize = ConfigManager.getInt("ametyhst.retry.queueSize", 65535);
        log.info("this message queue size is--{}", maxQueueSize);
        new Thread(new ConsumerTask(), "RetryMessageConsumeTask").start();

    }

    public void close() {
        this.isClosed = true;
        this.map.clear();

    }

    public boolean offer(RetryMessage<T> retryMessage) {
        if (isClosed) {
            return false;
        } else {
            synchronized (RetryMessageQueue.class) {
                for (int i = 0; i < 10 && !isClosed && currentSize > maxQueueSize; i++) {
                    if (i == 9) {
                        log.warn("the queue is full");
                        return false;
                    }
                    ThreadUtil.sleep(500l);
                }
                map.put(retryMessage.getId(), new SoftReference<>(retryMessage));
                ++currentSize;
                return true;
            }

        }


    }

    private class ConsumerTask implements Runnable {

        @Override
        public void run() {
            for (; !RetryMessageQueue.this.isClosed; ThreadUtil.sleep(10000L)) {
                Iterator<Map.Entry<Long, SoftReference<RetryMessage<T>>>> iterator = RetryMessageQueue.this.map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, SoftReference<RetryMessage<T>>> next = iterator.next();
                    if (!RetryMessageQueue.this.isClosed) {
                        break;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("retryConsumerQueue consumer task begins consumer the data--{}", next.getValue().get());
                    }
                    tryConsumer(next.getKey(), next.getValue());
                }
            }


        }

        private void tryConsumer(Long key, SoftReference<RetryMessage<T>> value) {
            RetryMessage<T> tRetryMessage = value.get();
            if (tRetryMessage != null) {
                if (System.currentTimeMillis() < tRetryMessage.getCurrentTimes()) {
                    return;
                }
                RetryMessageQueue.this.callback.apply(tRetryMessage);
            } else {
                RetryMessageQueue.this.map.remove(key);
            }
        }
    }


}
