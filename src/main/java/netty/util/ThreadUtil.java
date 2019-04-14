package netty.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadUtil {
    public static void sleep(long times){
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static ExecutorService getNewPool(String name, int corePoolSize, int maxPoolSize
            , int queueCapacity){
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(name + "-Thread-%d")
                .setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        log.error("the disruptor constructor by the thread --{}--occuring exception--{}",t.getName(),e);
                    }
                })
                .build();
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                100L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(queueCapacity),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());

    }
}
