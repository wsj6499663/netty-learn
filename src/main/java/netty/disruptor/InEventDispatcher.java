package netty.disruptor;

import netty.context.MessageContext;

public class InEventDispatcher extends AbstractEventDispatcher {
    @Override
    public void onEvent(MessageContext event, long sequence, boolean endOfBatch) throws Exception {

    }
}
