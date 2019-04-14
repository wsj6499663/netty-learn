package netty.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BrokerMetadataRequest {
    private String sarName;
}
