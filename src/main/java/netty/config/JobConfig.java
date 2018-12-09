package netty.config;

import lombok.Data;

@Data
public class JobConfig {
    private String sarName;
    private String jobName;
    private int type = 1;
    private int protocolType = 1;
    private String cron = "";
    private int shardingCount = 1;
    private String shardingItemParams = "";
    private String jobParams = "";
    private boolean failover;
    private boolean disabled;
    private boolean misfire;
    private boolean overwrite;
    private String jobHandlePath = "";
    private boolean streamingProcess;
    private boolean monitorExecution;
    private int shardingStrategy = 1;
    private String description = "";
}
