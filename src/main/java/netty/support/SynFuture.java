package netty.support;

import java.util.concurrent.*;

public class SynFuture<T> implements Future<T> {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private long beginTime = System.currentTimeMillis();
    private T response;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return countDownLatch.await(timeout, unit) ? response : null;
    }

    public void setResponse(T response) {
        this.response = response;
        countDownLatch.countDown();
    }

    public long getBeginTime() {
        return beginTime;
    }
}
