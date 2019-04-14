package netty.config;

import com.google.common.base.Predicates;
import netty.support.ApplicationSupport;
import org.springframework.util.StringUtils;

import java.util.Properties;

public class ConfigManager {
    private static ConfigManager configManager;
    private final PropertiesResolveService propertiesResolveService;
    private static final Properties properties = new Properties();

    private ConfigManager() {
        propertiesResolveService = ApplicationSupport.getBeanByType(PropertiesResolveService.class);
        Predicates.notNull().apply(propertiesResolveService);
    }

    private static String getValue(String key) {
        String value = getInstance().propertiesResolveService.getValue(key);
        if (StringUtils.isEmpty(value) && properties.contains(key)) {
            synchronized (Properties.class) {
                if (properties.contains(key)) {
                    value = properties.getProperty(key);
                }
            }
        }
        return value;
    }

    public static int getInt(String key, int value) {
        String vl = getValue(key);
        if (StringUtils.isEmpty(vl)) {
            return value;
        }
        return Integer.parseInt(vl);

    }

    public static String getString(String s, String value) {
        String vl = getValue(s);
        if (StringUtils.isEmpty(vl)) {
            return value;
        }
        return vl;
    }

    public static boolean getBoolean(String s, boolean disabled) {
        String vl = getValue(s);
        if (StringUtils.isEmpty(vl)) {
            return disabled;
        }
        return Boolean.valueOf(s);
    }


    private static class Inner {
        private static final ConfigManager config = new ConfigManager();
    }

    private static ConfigManager getInstance() {
        return Inner.config;
    }

}
