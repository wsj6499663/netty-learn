package netty.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobConfigBuilder {
    private static final String PREFIX = "amethyst";
    private static final String SPLIT = ".";
    private static final String TYPE = "type";
    private static final String CRON = "cron";
    private static final String SHARDING_TOTAL_COUNT = "shardingCount";
    private static final String SHARDING_ITEM_PARAMETERS = "shardingItemParams";
    private static final String JOB_PARAMETER = "jobParams";
    private static final String FAILOVER = "failover";
    private static final String DISABLED = "disabled";
    private static final String MISFIRE = "misfire";
    private static final String OVERWRITE = "overwrite";
    private static final String STREAMING_PROCESS = "streamingProcess";
    private static final String MONITOR_EXECUTION = "monitorExecution";
    private static final String SHARDING_STRATEGY = "shardingStrategy";
    private static final String DESCRIPTION = "description";
    private static final String PROTOCOL_TYPE = "protocolType";

}
