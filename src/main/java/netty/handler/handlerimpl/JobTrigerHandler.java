package netty.handler.handlerimpl;

import netty.consts.MsgEnum;
import netty.context.EventHandlerHolder;
import netty.handler.AbstractEventHandler;
import netty.message.MessageHead;
import netty.request.JobTriggerRequest;
import org.springframework.util.StringUtils;

public class JobTrigerHandler extends AbstractEventHandler<JobTriggerRequest> {
    @Override
    protected Class<JobTriggerRequest> resolveGeneric() {
        return JobTriggerRequest.class;
    }

    @Override
    protected void process(MessageHead messageHead, JobTriggerRequest jobTriggerRequest) {
        String jobName=jobTriggerRequest.getJobName();
        if(StringUtils.isEmpty(jobName)){

        }
        else{

        }
    }

    @Override
    public String type() {
        return MsgEnum.JOB_TRIGGER_REQ.getCode();
    }
}
