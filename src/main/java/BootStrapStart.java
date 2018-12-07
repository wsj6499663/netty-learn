import netty.context.EventHandlerHolder;
import netty.handler.EventHandler;
import netty.support.ApplicationSupport;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class BootStrapStart implements CommandLineRunner{
    @Override
    public void run(String... args){
        Map<String, EventHandler> map= ApplicationSupport.getApp().getBeansOfType(EventHandler.class);
        map.forEach((k,v)->{
            EventHandlerHolder.put(k,v);
        });
    }

}
