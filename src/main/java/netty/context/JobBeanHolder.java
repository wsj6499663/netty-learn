package netty.context;

import com.google.common.base.Predicates;
import netty.handler.job.JobHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JobBeanHolder {
    private final ConcurrentHashMap<String, JobHandler> map = new ConcurrentHashMap<>();

    private static class Inner {
        private static final JobBeanHolder job = new JobBeanHolder();
    }

    /**
     *
     * @param name job Name
     * @param jobHandler
     */
    public static void put(String name, JobHandler jobHandler) {
        Predicates.notNull().apply(name);
        Predicates.notNull().apply(jobHandler);
        Inner.job.map.put(name, jobHandler);

    }

    public static Set<Map.Entry<String, JobHandler>> entrySet() {
        Set<Map.Entry<String, JobHandler>> entries = Inner.job.map.entrySet();
        return entries;
    }


}
