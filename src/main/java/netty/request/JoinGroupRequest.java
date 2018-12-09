package netty.request;

import lombok.Data;
import netty.config.JobConfig;

import java.util.List;
@Data
public class JoinGroupRequest {
    private String sarName;
    private List<JobConfig> jobs;
}
