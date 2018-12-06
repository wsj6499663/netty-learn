package netty.message;

import lombok.Data;

@Data
public class MessageHead {
    private String clientId;
    private String hostname;
    private String sessionId;
    private long requestId;
    private String type;
    private int version;
}
