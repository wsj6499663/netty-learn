package netty.handler.event;
import netty.context.MessageContext;
import netty.message.MessageHead;
import netty.util.ProtobufUtil;
public abstract class AbstractEventHandler<T> implements EventHandler<MessageContext> {
    @Override
    public void handler(MessageContext var) {
        T t=ProtobufUtil.deserialize(var.getBody(),resolveGeneric());
        assert t!=null;
        this.process(var.getMessageHead(),t);

    }

    protected  abstract Class<T> resolveGeneric();
    protected  abstract void process(MessageHead messageHead,T t);
}
