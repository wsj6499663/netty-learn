package netty.support;

import lombok.extern.slf4j.Slf4j;
import netty.config.ConfigManager;
import netty.message.RetryMessage;
import netty.util.ThreadUtil;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class RetryMessageQueue<T> {
    private volatile boolean isClosed = false;
    private DelayQueue<RetryMessage<T>> delayQueue = new DelayQueue<>();
    private Function<RetryMessage<T>, Object> callback;
    private int maxQueueSize;
    private volatile int currentSize;

    public RetryMessageQueue(Function<RetryMessage<T>, Object> callback) {
        this.callback = callback;
    }

    public void init() {
        this.maxQueueSize = ConfigManager.getInt("ametyhst.retry.queueSize", 65535);
        log.info("this message queue size is--{}", maxQueueSize);
        if (log.isDebugEnabled()) {
            log.debug("begin executor the retryQueue consumer");
        }
        new Thread(new ConsumerTask(), "RetryMessageConsumeTask").start();

    }

    public void close() {
        this.isClosed = true;
        delayQueue.clear();

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
                delayQueue.put(retryMessage);
                ++currentSize;
                return true;
            }

        }


    }

    private class ConsumerTask implements Runnable {

        @Override
        public void run() {
            while (!RetryMessageQueue.this.isClosed && !Thread.currentThread().isInterrupted()) {
                try {
                    final RetryMessage<T> take = delayQueue.poll(200, TimeUnit.MILLISECONDS);
                    if (take.get() != null) {
                        if (take.getCurrentTimes() <= take.getDefaultTimes()) {
                            int currentTimes = take.getCurrentTimes() + 1;
                            take.setCurrentTimes(currentTimes);
                        }
                        tryConsumer(take);
                        take.resetTimeOut();
                        delayQueue.offer(take);
                    }
                } catch (InterruptedException e) {
                    log.error("interrupt--{}", e);
                    Thread.currentThread().interrupt();
                    close();
                }
            }


        }

        private void tryConsumer(RetryMessage<T> value) {
            RetryMessageQueue.this.callback.apply(value);

        }
    }


}
