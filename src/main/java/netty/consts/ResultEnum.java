package netty.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResultEnum {
    ARGS_INVALID(2001, "args invalid.");
    @Getter
    private Integer code;
    @Getter
    private String desc;


}
