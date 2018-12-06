package netty.context;

import lombok.Data;
import netty.message.MessageHead;

@Data
public class MessageContext {
    private byte[] body;
    private MessageHead messageHead;
}
