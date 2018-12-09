package netty.support;

import lombok.extern.slf4j.Slf4j;
import netty.consts.MsgEnum;
import netty.response.BrokerMetadataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AmethystClientHelper implements Closeable {
    private volatile boolean isConnected = false;
    private ConcurrentHashMap<Long, SynFuture<BrokerMetadataResponse>> concurrentHashMap = new ConcurrentHashMap<>();
    @Autowired
    private UniqueIdGeneratorService uniqueIdGeneratorService;

    AmethystClientHelper() {

    }


    @Override
    public void close() throws IOException {

    }

    public <T> void send(MsgEnum jobLogReq, long id, T data) {
        if (isConnected) {
            sendInternal(jobLogReq.getCode(), id, data);
        } else {
            log.error("the connect has already disable");
            throw new RuntimeException("connect is disable");
        }
    }

    private <T> void sendInternal(String code, long id, T data) {
    }

    private class cleanTask implements Runnable {

        @Override
        public void run() {

        }
    }
}
