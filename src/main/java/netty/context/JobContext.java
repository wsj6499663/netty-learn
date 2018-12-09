package netty.context;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class JobContext {
    private int shardingCount;
    private List<Integer> shardingItems = new ArrayList();
    private Map<Integer, String> shardingItemParameters = new HashMap();
    private String jobParameter;
    private String executionSource;

    private JobContext(int shardingCount, List<Integer> shardingItems, Map<Integer, String> shardingItemParameters, String jobParameter, String executionSource) {
        this.shardingCount = shardingCount;
        this.shardingItems = shardingItems;
        this.shardingItemParameters = shardingItemParameters;
        this.jobParameter = jobParameter;
        this.executionSource = executionSource;
    }

    public String getBizTime() {
        if (StringUtils.isEmpty(jobParameter)) {
            return null;
        }
        Map<String, String> collect = Arrays.asList(jobParameter.split(",")).stream().map(str -> str.split("=")).collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
        return collect.get("bizTime");
    }

    public static class JobContextBuilder {
        private int shardingCount;
        private List<Integer> shardingItems;
        private Map<Integer, String> shardingItemParameters;
        private String jobParameter;
        private String executionSource;

        private JobContextBuilder() {
        }

        public JobContext.JobContextBuilder shardingCount(int shardingCount) {
            this.shardingCount = shardingCount;
            return this;
        }

        public JobContext.JobContextBuilder shardingItems(List<Integer> shardingItems) {
            this.shardingItems = shardingItems;
            return this;
        }

        public JobContext.JobContextBuilder shardingItemParameters(Map<Integer, String> shardingItemParameters) {
            this.shardingItemParameters = shardingItemParameters;
            return this;
        }

        public JobContext.JobContextBuilder jobParameter(String jobParameter) {
            this.jobParameter = jobParameter;
            return this;
        }

        public JobContext.JobContextBuilder executionSource(String executionSource) {
            this.executionSource = executionSource;
            return this;
        }

        public JobContext build() {
            return new JobContext(this.shardingCount, this.shardingItems, this.shardingItemParameters, this.jobParameter, this.executionSource);
        }

        public String toString() {
            return "JobContext.JobContextBuilder(shardingCount=" + this.shardingCount + ", shardingItems=" + this.shardingItems + ", shardingItemParameters=" + this.shardingItemParameters + ", jobParameter=" + this.jobParameter + ", executionSource=" + this.executionSource + ")";
        }
    }
}
