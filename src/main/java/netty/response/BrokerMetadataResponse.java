package netty.response;

import lombok.Data;

@Data
public class BrokerMetadataResponse extends CommonResponse {
    private long requestId;
    private String broker;
}
