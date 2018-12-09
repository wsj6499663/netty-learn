package netty.core;

import netty.request.JobTriggerRequest;

public interface AmethystJob {
    void execute(JobTriggerRequest jobTriggerRequest);
}
