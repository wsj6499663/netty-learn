package netty.util;

import netty.config.ConfigManager;
import org.springframework.util.StringUtils;

public class NameUtil {
    private static volatile String cachedName;

    public static String getName() {
        if (!StringUtils.isEmpty(cachedName)) {
            return cachedName;
        } else {
            synchronized (NameUtil.class) {
                if (StringUtils.isEmpty(cachedName)) {
                    String application = ConfigManager.getValue("spring.application.name", "");
                    if (!StringUtils.isEmpty(application)) {
                        cachedName = application;
                        return cachedName;
                    }
                    cachedName = ConfigManager.getValue("sar.name", "amethyst-client");
                    return cachedName;
                }
            }
            return cachedName;
        }
    }
}
