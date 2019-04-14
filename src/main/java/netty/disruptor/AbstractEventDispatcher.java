package netty.disruptor;

import com.lmax.disruptor.EventHandler;
import netty.context.MessageContext;

/**
 * 消费触发事件
 */
public abstract class AbstractEventDispatcher implements EventHandler<MessageContext> {
    /**
     *
     * @param event ringbuffer 持有数据
     * @param sequence
     * @param endOfBatch
     * @throws Exception
     */
    @Override
    abstract public void onEvent(MessageContext event, long sequence, boolean endOfBatch) throws Exception;

}
