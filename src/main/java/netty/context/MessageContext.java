package netty.context;

import lombok.Data;
import netty.message.MessageHead;

/**
 * 消息传输(netty distuptor)
 */
@Data
public class MessageContext {
    /**
     * 消息内容
     */
    private byte[] body;
    /**
     * 消息标识
     */
    private MessageHead head;

    public MessageContext() {
        head = new MessageHead();
    }

    public MessageContext(String type) {
        this();
        this.head.setType(type);
    }

    public MessageContext(String type, byte[] body) {
        this(type);
        this.body = body;

    }

    public MessageContext(String type, Long requestId, byte[] body) {
        this(type, body);
        this.head.setRequestId(requestId);
    }
    public String getClientId() {
        return this.head.getClientId();
    }

    public void setClientId(String clientId) {
        this.head.setClientId(clientId);
    }

    public String getSessionId() {
        return this.head.getSessionId();
    }

    public void setSessionId(String sessionId) {
        this.head.setSessionId(sessionId);
    }

    public long getRequestId() {
        return this.head.getRequestId();
    }

    public void setRequestId(long requestId) {
        this.head.setRequestId(requestId);
    }

    public String getType() {
        return this.head.getType();
    }

    public void setType(String type) {
        this.head.setType(type);
    }

    public int getVersion() {
        return this.head.getVersion();
    }

    public void setVersion(int version) {
        this.head.setVersion(version);
    }

    public void setHostname(String hostname) {
        this.head.setHostname(hostname);
    }

    public String getHostname() {
        return this.head.getHostname();
    }
}
