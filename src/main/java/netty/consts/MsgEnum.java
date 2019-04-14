package netty.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import netty.request.BrokerMetadataRequest;
import netty.request.JobLogRequest;
import netty.request.JobTriggerRequest;
import netty.request.JoinGroupRequest;
import netty.response.BrokerMetadataResponse;
import netty.response.JobTriggerResponse;

@AllArgsConstructor
@Getter
public enum MsgEnum {
    JOB_TRIGGER_REQ("06", JobTriggerRequest.class, "07", "job trigger request"),
    JOB_LOG_REQ("08", JobLogRequest.class, "09", "job log request"),
    JOIN_GROUP_REQ("04", JoinGroupRequest.class, "05", "join group request"),
    JOB_TRIGGER_RESP("07", JobTriggerResponse.class, null, "job trigger response"),
    BROKER_METADATA_REQ("02", BrokerMetadataRequest.class, "03", "broker metadata request"),
    /**
     * heartbeat request message type
     */
    HEARTBEAT_REQ("00", null, "01", "heartbeat request"),
    /**
     * heartbeat response message type
     */
    HEARTBEAT_RESP("01", null, null, "heartbeat response"),
    /**
     * broker metadata response message type
     */
    BROKER_METADATA_RESP("03", BrokerMetadataResponse.class, null, "broker metadata response");


    private String code;

    private Class clz;

    private String nextCode;

    private String memo;


}
