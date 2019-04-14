package netty.disruptor;

import lombok.Builder;
import lombok.Getter;

/**
 * 配置disruptor producer ringbuffer eventhandler 线程池相关参数
 */
@Builder(toBuilder = true)
@Getter
public class DisruptorConfig {
    /**
     * 线程名称
     **/
    private String name;
    /**
     * ringbuffer 长度
     */
    private int bufferSize = 2 << 13;
    /**
     * 消息线程池最大线程数
     */
    private int maximumPoolSize = 10;

    /**
     * 消息线程池核心线程数
     */
    private int corePoolSize = 3;

    /**
     * 消息线程池空闲时间
     */
    private int keepAliveTime = 120;
    /**
     * 消息线程池任务队列容量
     */
    private int queueSize = 100;
    /**
     * 队列消费者
     */
    private AbstractEventDispatcher dispatcher;

    /**
     * 消费后处理
     */
    private AbstractEventDispatcher post;
    /**
     * 消费异常处理
     */
    private AbstractExceptionHandler errorHandler;
}
