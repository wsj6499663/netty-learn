package netty.config;

import netty.support.ApplicationSupport;
import org.springframework.util.StringUtils;

import java.util.Properties;

public class ConfigManager {
    private static ConfigManager configManager;
    private final PropertiesResolveService propertiesResolveService;
    private static final Properties properties = new Properties();

    private ConfigManager() {
        propertiesResolveService = ApplicationSupport.getBeanByType(PropertiesResolveService.class);
        assert propertiesResolveService != null;
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

    public static String getValue(String key, String value) {
        assert value != null;
        String valueTemp = getValue(key);
        return StringUtils.isEmpty(valueTemp) ? value : valueTemp;
    }

    public static int getInt(String key, int value) {
        String vl = getValue(key);
        if (StringUtils.isEmpty(vl)) {
            return value;
        }
        return Integer.parseInt(vl);

    }


    private static class Inner {
        private Inner() {
        }

        private static final ConfigManager config = new ConfigManager();
    }

    private static ConfigManager getInstance() {
        return Inner.config;
    }

}