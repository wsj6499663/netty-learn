package netty.context;

import netty.handler.job.JobHandler;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JobBeanHolder {
    private final ConcurrentHashMap<String, JobHandler> map = new ConcurrentHashMap<>();

    private static class Inner {
        private static final JobBeanHolder job = new JobBeanHolder();
    }

    public static void put(String name, JobHandler jobHandler) {
        assert !StringUtils.isEmpty(name);
        assert jobHandler != null;
        Inner.job.map.put(name, jobHandler);

    }

    public static Set<Map.Entry<String, JobHandler>> entrySet() {
        Set<Map.Entry<String, JobHandler>> entries = Inner.job.map.entrySet();
        return entries;
    }


}
