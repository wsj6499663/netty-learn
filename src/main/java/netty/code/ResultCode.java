package netty.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCode {
    /**
     * redirect to another server result code
     */
    REDIRECT(3020, "redirect"),

    SUCCESS(1000,"success");


    private Integer code;
    private String desc;

}
