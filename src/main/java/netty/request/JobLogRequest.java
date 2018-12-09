package netty.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class JobLogRequest {
    private String sarName;
    private String jobName;
    private long execId;
    private long flowExecId;
    private String hostname;
    private int shardingCount;
    private List<Integer> shardingItems;
    private Map<Integer, String> shardingItemParameters;
    private String jobParameter;
    private String executionSource;
    private String failureCause;
    private int status;
    private long startTime;
    private long completeTime;
}
