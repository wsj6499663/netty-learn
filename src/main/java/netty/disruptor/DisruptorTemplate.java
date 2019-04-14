package netty.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import lombok.extern.slf4j.Slf4j;
import netty.context.MessageContext;
import netty.error.AmethystException;
import netty.util.ThreadUtil;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Slf4j
public class DisruptorTemplate implements DisruptorQueue<MessageContext> {

    private DisruptorConfig config;
    /**
     * push event to the ringbuffer
     */
    private Disruptor<MessageContext> disruptor;

    private RingBuffer<MessageContext> ringBuffer;
    private ExecutorService executorService;

    public DisruptorTemplate(DisruptorConfig disruptorConfig) {
        this.config = disruptorConfig;
        disruptorInit();
    }

    private void disruptorInit() {
        //用于填充ringbuffer
        MsgFactory factory = new MsgFactory();
        /**
         * 多线程执行eventProcess
         * 在run方法内执行evenhandler处理可用的messagecontext，注意异常的显示处理，不可往外抛出
         */

        executorService = ThreadUtil.getNewPool(config.getName(), config.getCorePoolSize()
                , config.getMaximumPoolSize(), config.getQueueSize());
        /**
         * 支持多disruptor
         */
        disruptor = new Disruptor<>(factory, config.getBufferSize(), executorService);
        if (null == config.getDispatcher()) {
            log.error("Dispatcher can't be null.");
            throw new AmethystException("Dispatcher can't be null.");
        }
        EventHandlerGroup<MessageContext> handlerGroup = disruptor.handleEventsWith(config.getDispatcher());
        //消费后处理
        if (null != config.getPost()) {
            handlerGroup.then(config.getPost());
        }

        if (null != config.getErrorHandler()) {
            disruptor.handleExceptionsFor(config.getDispatcher()).with(config.getErrorHandler());
        }
        disruptor.start();
        //返回填充好messagecontext的ringbuffer
        //disruptor publish 后触发eventhandler
        ringBuffer = disruptor.getRingBuffer();

    }

    @Override
    public void push(MessageContext message) {
        log.info("begin push event");
        //取到已经被消费的messageContext的桶位,填充信息的数据
        long sequence = ringBuffer.next();
        try {
            // Get the entry in the Disruptor
            MessageContext newMessage = ringBuffer.get(sequence);
            // Fill with data
            newMessage.setRequestId(message.getRequestId());
            newMessage.setClientId(message.getClientId());
            newMessage.setHostname(message.getHostname());
            newMessage.setSessionId(message.getSessionId());
            newMessage.setVersion(message.getVersion());
            newMessage.setType(message.getType());
            newMessage.setBody(message.getBody());
        } finally {
            log.debug("ringbuffer publish after");
            ringBuffer.publish(sequence);
        }
    }

    @Override
    public void close() throws IOException {
        //This method will not shutdown the executor, nor will it await the final termination of the processor threads
        disruptor.shutdown();
        executorService.shutdown();
    }
}
