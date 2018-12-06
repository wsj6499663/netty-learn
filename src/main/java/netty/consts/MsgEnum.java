package netty.consts;

import lombok.Getter;
import netty.request.JobTriggerRequest;

public enum MsgEnum {
    JOB_TRIGGER_REQ("06", JobTriggerRequest.class, "07", "job trigger request"),
    ;
    private String code;
    private Class clz;
    private String nextCode;
    private String memo;

    private MsgEnum(String code, Class clz, String nextCode, String memo) {
        this.code = code;
        this.clz = clz;
        this.nextCode = nextCode;
        this.memo = memo;
    }

    public String getCode() {
        return code;
    }

    public Class getClz() {
        return clz;
    }

    public String getNextCode() {
        return nextCode;
    }

    public String getMemo() {
        return memo;
    }
}
