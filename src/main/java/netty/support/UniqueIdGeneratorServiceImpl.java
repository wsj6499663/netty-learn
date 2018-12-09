package netty.support;

import netty.util.IdWorkerUtil;
import netty.util.LocalHostUtil;
import netty.util.NameUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UniqueIdGeneratorServiceImpl implements UniqueIdGeneratorService {
    private IdWorkerUtil idWorkerUtil;

    @PostConstruct
    public void init() {
        String ip = LocalHostUtil.getLocalIp();
        long dataCenterId=Math.abs(ip.hashCode()%32);
        long wordId= Math.abs(NameUtil.getName().hashCode()%32);
        idWorkerUtil = new IdWorkerUtil(wordId,dataCenterId,0l);
    }

    @Override
    public long nextId() {
        return idWorkerUtil.getId();
    }
}
