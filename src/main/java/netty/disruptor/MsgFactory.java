package netty.disruptor;

import com.lmax.disruptor.EventFactory;
import netty.context.MessageContext;

public class MsgFactory  implements EventFactory<MessageContext> {
    @Override
    public MessageContext newInstance() {
        return new MessageContext();
    }
}
