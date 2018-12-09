package netty.config;

import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import netty.consts.JobTypeEnum;
import netty.context.JobBeanHolder;
import netty.handler.job.JobHandler;
import netty.request.JoinGroupRequest;
import netty.util.NameUtil;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private static volatile JoinGroupRequest joinGroupRequest = new JoinGroupRequest();

    public static JoinGroupRequest build() {
        if (CollectionUtils.isEmpty(joinGroupRequest.getJobs())) {
            synchronized (JobConfigBuilder.class) {
                if (CollectionUtils.isEmpty(joinGroupRequest.getJobs())) {
                    List<JobConfig> jobs = Lists.newArrayList();
                    Iterator<Map.Entry<String, JobHandler>> iterator = JobBeanHolder.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JobHandler> me = iterator.next();
                        JobConfig jobConfig = fill(me.getKey(), me.getValue());
                        jobs.add(jobConfig);
                    }
                    joinGroupRequest.setSarName(NameUtil.getName());
                    joinGroupRequest.setJobs(jobs);
                }
            }
        }
        return joinGroupRequest;
    }

    private static JobConfig fill(String jobName, JobHandler jobHandler) {
        JobConfig jobConfig = new JobConfig();
        String prefix = "amethyst." + jobName + ".";
        jobConfig.setJobName(jobName);
        jobConfig.setJobHandlePath(jobHandler.getClass().getName());
        jobConfig.setSarName(NameUtil.getName());
        jobConfig.setType(ConfigManager.getInt(prefix + "type", jobConfig.getType()));
        jobConfig.setCron(ConfigManager.getString(prefix + "cron", jobConfig.getCron()));
        if (jobConfig.getType() == JobTypeEnum.TIMER.getCode() && StringUtil.isNullOrEmpty(jobConfig.getCron())) {
            jobConfig.setCron("0 0 3 * * ?");
            log.warn("This job is a timer job, but cron is null, use default corn is [{}]", "0 0 3 * * ?");
        }
        jobConfig.setDescription(ConfigManager.getString(prefix + "description", jobConfig.getDescription()));
        jobConfig.setDisabled(ConfigManager.getBoolean(prefix + "disabled", jobConfig.isDisabled()));
        jobConfig.setFailover(ConfigManager.getBoolean(prefix + "failover", jobConfig.isFailover()));
        jobConfig.setJobParams(ConfigManager.getString(prefix + "jobParams", jobConfig.getJobParams()));
        jobConfig.setShardingStrategy(ConfigManager.getInt(prefix + "shardingStrategy", jobConfig.getShardingStrategy()));
        jobConfig.setMisfire(ConfigManager.getBoolean(prefix + "misfire", jobConfig.isMisfire()));
        jobConfig.setMonitorExecution(ConfigManager.getBoolean(prefix + "monitorExecution", jobConfig.isMonitorExecution()));
        jobConfig.setOverwrite(ConfigManager.getBoolean(prefix + "overwrite", jobConfig.isOverwrite()));
        jobConfig.setShardingItemParams(ConfigManager.getString(prefix + "shardingItemParams", jobConfig.getShardingItemParams()));
        jobConfig.setShardingCount(ConfigManager.getInt(prefix + "shardingCount", jobConfig.getShardingCount()));
        jobConfig.setStreamingProcess(ConfigManager.getBoolean(prefix + "streamingProcess", jobConfig.isStreamingProcess()));
        jobConfig.setProtocolType(ConfigManager.getInt(prefix + "protocolType", jobConfig.getProtocolType()));
        return jobConfig;

    }
}
