package netty.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import netty.context.MessageContext;

/**
 *消费事件异常处理
 */
public  abstract  class AbstractExceptionHandler  implements ExceptionHandler<MessageContext> {
    @Override
    public void handleEventException(Throwable ex, long sequence, MessageContext event) {

    }

    @Override
    public void handleOnStartException(Throwable ex) {

    }

    @Override
    public void handleOnShutdownException(Throwable ex) {

    }
}
