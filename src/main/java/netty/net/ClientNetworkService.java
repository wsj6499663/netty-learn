package netty.net;

import netty.consts.MsgEnum;
import netty.message.RetryMessage;
import netty.support.AmethystClientHelper;
import netty.support.RetryMessageQueue;
import netty.support.UniqueIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientNetworkService {

    private final UniqueIdGeneratorService uniqueIdGeneratorService;
    private final RetryMessageQueue<Object> retryMessageQueue;
    private final AmethystClientHelper amethystClientHelper;

    @Autowired
    public ClientNetworkService(UniqueIdGeneratorService uniqueIdGeneratorService,AmethystClientHelper amethystClientHelper) {
        this.amethystClientHelper=amethystClientHelper;
        this.uniqueIdGeneratorService=uniqueIdGeneratorService;
        this.retryMessageQueue=new RetryMessageQueue<>(msg->{
            send(MsgEnum.JOB_LOG_REQ,msg.getId(),msg.getData());
            return true;
        });
    }

    public void init(){
        synchronized (ClientNetworkService.class){
            retryMessageQueue.init();
        }
    }

    public void send(MsgEnum jobLogReq, long id, Object data) {
        amethystClientHelper.send(jobLogReq,id,data);
    }

    public void sendAndRetry(MsgEnum jobLogReq, Object data) {
        long id=uniqueIdGeneratorService.nextId();
        RetryMessage<Object> message = new RetryMessage(id, data);
         retryMessageQueue.offer(message);


    }
}
