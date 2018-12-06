package netty.response;

import lombok.Data;

import java.util.List;
@Data
public class JobReponse extends CommonResponse {
    private String sarName;
    private String jobName;
    private long execId;
    private long flowExecId;
    private List<Integer> shardingItems;
    private String jobParameter;
}
