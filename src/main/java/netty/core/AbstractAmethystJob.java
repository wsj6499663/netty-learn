package netty.core;

import lombok.extern.slf4j.Slf4j;
import netty.consts.JobDetailStatusEnum;
import netty.consts.MsgEnum;
import netty.net.ClientNetworkService;
import netty.request.JobLogRequest;
import netty.request.JobTriggerRequest;
import netty.util.LocalHostUtil;
import org.springframework.beans.BeanUtils;

@Slf4j
public abstract class AbstractAmethystJob implements Runnable, AmethystJob {
    private  final ClientNetworkService clientNetworkService;
    AbstractAmethystJob(ClientNetworkService clientNetworkService){
        this.clientNetworkService=clientNetworkService;
    }
    @Override
    public void execute(JobTriggerRequest jobTriggerRequest) {
        if (log.isDebugEnabled()) {
            log.debug("begin execute the job--", jobTriggerRequest.getJobName());
        }
        beforeExecute(jobTriggerRequest);
        executeJobInternal(jobTriggerRequest);

    }

    private void beforeExecute(JobTriggerRequest jobTriggerRequest) {
        reportLog(jobTriggerRequest, JobDetailStatusEnum.TASK_RUNNING.getCode(), "");
    }

    private void reportLog(JobTriggerRequest jobTriggerRequest, Integer code, String msg) {
        JobLogRequest jobLogRequest = getJobLog(jobTriggerRequest, code, msg);
        log.info("request data--{}",jobLogRequest);
        clientNetworkService.sendAndRetry(MsgEnum.JOB_LOG_REQ,jobLogRequest);
    }

    private JobLogRequest getJobLog(JobTriggerRequest jobTriggerRequest, Integer code, String msg) {
        JobLogRequest jobLogRequest = new JobLogRequest();
        BeanUtils.copyProperties(jobTriggerRequest, jobLogRequest);
        jobLogRequest.setHostname(LocalHostUtil.getLocalIp());
        jobLogRequest.setStatus(code);
        jobLogRequest.setFailureCause(msg);
        if (JobDetailStatusEnum.TASK_STAGING.getCode().intValue() == code.intValue()) {
            jobLogRequest.setStartTime(System.currentTimeMillis());
        }
        if (JobDetailStatusEnum.TASK_FINISHED.getCode().intValue() == code.intValue()) {
            jobLogRequest.setCompleteTime(System.currentTimeMillis());
        }
        return jobLogRequest;

    }

    private void executeJobInternal(JobTriggerRequest jobTriggerRequest) {
    }
}
