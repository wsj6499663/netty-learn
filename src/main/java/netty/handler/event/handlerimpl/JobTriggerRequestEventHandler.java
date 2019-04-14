package netty.handler.event.handlerimpl;

import lombok.extern.slf4j.Slf4j;
import netty.consts.MsgEnum;
import netty.consts.ResultEnum;
import netty.handler.event.AbstractEventHandler;
import netty.message.MessageHead;
import netty.net.ClientNetworkService;
import netty.request.JobTriggerRequest;
import netty.response.JobTriggerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JobTriggerRequestEventHandler extends AbstractEventHandler<JobTriggerRequest> {

    @Autowired
    private ClientNetworkService clientNetworkService;


    @Override
    protected Class<JobTriggerRequest> resolveGeneric() {
        return JobTriggerRequest.class;
    }

    @Override
    protected void process(MessageHead messageHead, JobTriggerRequest jobTriggerRequest) {
        String jobName = jobTriggerRequest.getJobName();
        if (StringUtils.isEmpty(jobName)) {
            report(messageHead.getRequestId(), jobTriggerRequest, ResultEnum.ARGS_INVALID, "can not find the jobName==" + jobName);
        } else {
            executor(jobTriggerRequest);
        }
    }

    private void executor(JobTriggerRequest jobTriggerRequest) {
//        getInstance().submit()
    }

    private void report(long requestId, JobTriggerRequest data, ResultEnum codeEnum, String s) {
        JobTriggerResponse jobTriggerResponse = new JobTriggerResponse();
        jobTriggerResponse.setResultCode(codeEnum.getCode());
        jobTriggerResponse.setResultMsg(codeEnum.getCode() + s);
        jobTriggerResponse.setSarName(data.getSarName());
        jobTriggerResponse.setJobName(data.getJobName());
        jobTriggerResponse.setExecId(data.getExecId());
        jobTriggerResponse.setFlowExecId(data.getFlowExecId());
        jobTriggerResponse.setShardingItems(data.getShardingItems());
        jobTriggerResponse.setJobParameter(data.getJobParameter());
        log.debug("JobTriggerResponse requestId:{}, {}", requestId, jobTriggerResponse);
        clientNetworkService.send(MsgEnum.JOB_TRIGGER_RESP, requestId, jobTriggerResponse);
    }

    @Override
    public String type() {
        return MsgEnum.JOB_TRIGGER_REQ.getCode();
    }
}
