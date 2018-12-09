package netty.consts;

import netty.request.BrokerMetadataRequest;
import netty.request.JobLogRequest;
import netty.request.JobTriggerRequest;
import netty.request.JoinGroupRequest;
import netty.response.JobTriggerResponse;

public enum MsgEnum {
    JOB_TRIGGER_REQ("06", JobTriggerRequest.class, "07", "job trigger request"),
    JOB_LOG_REQ("08", JobLogRequest.class, "09", "job log request"),
    JOIN_GROUP_REQ("04", JoinGroupRequest.class, "05", "join group request"),
    JOB_TRIGGER_RESP("07", JobTriggerResponse.class, (String)null, "job trigger response"),
    BROKER_METADATA_REQ("02",BrokerMetadataRequest .class, "03", "broker metadata request");


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
