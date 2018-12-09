package netty.handler.job;

import netty.context.JobContext;

public interface JobHandler {
    void execute(JobContext jobContext);
}
