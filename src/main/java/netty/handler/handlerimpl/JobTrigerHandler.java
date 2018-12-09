package netty.handler.handlerimpl;

import lombok.extern.slf4j.Slf4j;
import netty.consts.MsgEnum;
import netty.consts.ResultEnum;
import netty.handler.AbstractEventHandler;
import netty.message.MessageHead;
import netty.request.JobTriggerRequest;
import netty.response.JobReponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class JobTrigerHandler extends AbstractEventHandler<JobTriggerRequest> {
    private volatile ExecutorService executorService;

    private static class Inner{
        private static final ExecutorService ex= Executors.newCachedThreadPool();
    }
    private  ExecutorService getInstance(){
        return Inner.ex;
    }

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
        JobReponse jobTriggerResponse = new JobReponse();
        jobTriggerResponse.setResultCode(codeEnum.getCode());
        jobTriggerResponse.setResultMsg(codeEnum.getCode() + s);
        jobTriggerResponse.setSarName(data.getSarName());
        jobTriggerResponse.setJobName(data.getJobName());
        jobTriggerResponse.setExecId(data.getExecId());
        jobTriggerResponse.setFlowExecId(data.getFlowExecId());
        jobTriggerResponse.setShardingItems(data.getShardingItems());
        jobTriggerResponse.setJobParameter(data.getJobParameter());
        log.debug("JobTriggerResponse requestId:{}, {}", requestId, jobTriggerResponse);

    }

    @Override
    public String type() {
        return MsgEnum.JOB_TRIGGER_REQ.getCode();
    }
}
