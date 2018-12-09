package netty.context;

import netty.handler.event.EventHandler;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventHandlerHolder {
    private Map<String, EventHandler<MessageContext>> map = new ConcurrentHashMap<>();

    public static void put(String type, EventHandler<MessageContext> eventHandler) {
        assert !StringUtils.isEmpty(type);
        getInstance().map.put(type, eventHandler);
    }

    //multiple thread lazy style
    private static class Inner {
        private static final EventHandlerHolder eventHandlerHolder = new EventHandlerHolder();

    }

    private static EventHandlerHolder getInstance() {
        return Inner.eventHandlerHolder;
    }

    private EventHandlerHolder() {

    }

    public static EventHandler<MessageContext> get(String name) {
        assert name != null;
        return getInstance().map.get(name);
    }

}
