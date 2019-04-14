package netty.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class ApplicationSupport implements ApplicationContextAware {
    private static ApplicationContext app;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        app = applicationContext;
    }

    public static Object getBeanByName(String beanName) {
        assert app != null;
        return app.getBean(beanName);
    }

    public static ApplicationContext getApp() {
        assert app != null;
        return app;
    }

    public static <T> T getBeanByType(Class<T> cls) {
        assert app != null;
        return app.getBean(cls);
    }

}
