package netty.response;

import lombok.Data;

@Data
public class CommonResponse {
    private int resultCode;
    private String resultMsg;
}
