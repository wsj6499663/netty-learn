package netty.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JobTriggerRequest {
    private String sarName;
    private String jobName;
    private String jobHandlePath;
    private long execId;
    private long flowExecId;
    private int shardingCount;
    private List<Integer> shardingItems;
    private Map<Integer, String> shardingItemParameters;
    private String jobParameter;
    private String executionSource;
}
