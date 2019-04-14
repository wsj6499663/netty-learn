import netty.context.EventHandlerHolder;
import netty.handler.event.EventHandler;
import netty.net.ClientNetworkService;
import netty.support.ApplicationSupport;
import netty.support.UniqueIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
//if we  implements this CommandLine that the has been in the container
public class BootStrapStart implements CommandLineRunner {
    @Autowired
    private ClientNetworkService clientNetworkService;
    @Autowired
    private UniqueIdGeneratorService uniqueIdGeneratorService;

    @Override
    public void run(String... args) {
        Map<String, EventHandler> map = ApplicationSupport.getApp().getBeansOfType(EventHandler.class);
        map.forEach((k, v) -> {
            EventHandlerHolder.put(k, v);
        });
        clientNetworkService.init();
        uniqueIdGeneratorService.init();

    }

}
