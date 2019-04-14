package netty.net;

import netty.config.ConfigManager;
import netty.consts.MsgEnum;
import netty.context.MessageContext;
import netty.disruptor.*;
import netty.message.RetryMessage;
import netty.protobuf.MessagePackProtobuf;
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
    private volatile DisruptorQueue<MessageContext> inputQueue;
    private final AbstractEventDispatcher inEventDispatcher;
    private final AbstractExceptionHandler eventExceptionHandler;

    @Autowired
    public ClientNetworkService(UniqueIdGeneratorService uniqueIdGeneratorService,
                                AmethystClientHelper amethystClientHelper,
                                InEventDispatcher inEventDispatcher,
                                EventExceptionHandler eventExceptionHandler
    ) {
        this.amethystClientHelper = amethystClientHelper;
        this.uniqueIdGeneratorService = uniqueIdGeneratorService;
        this.retryMessageQueue = new RetryMessageQueue<>(msg -> {
            send(MsgEnum.JOB_LOG_REQ, msg.getId(), msg.getData());
            return true;
        });
        this.inEventDispatcher = inEventDispatcher;
        this.eventExceptionHandler = eventExceptionHandler;
    }

    /**
     * start the retryMessageQueue thread
     */
    public void init() {
        if (inputQueue == null)
            synchronized (ClientNetworkService.class) {
                if (inputQueue == null) {
                    //初始化disruptor
                    DisruptorConfig input = DisruptorTemplateBuilder.newBuilder("input")
                            .bufferSize(ConfigManager.getInt("ametyhst.input.bufferSize", 2 << 15))
                            .dispatcher(inEventDispatcher)
                            .errorHandler(eventExceptionHandler).build();
                    inputQueue = new DisruptorTemplate(input);
                    //启动重试线程
                    retryMessageQueue.init();
                }

            }
        //init connect
        amethystClientHelper.init(this);
    }

    public void send(MsgEnum jobLogReq, long id, Object data) {
        amethystClientHelper.send(jobLogReq, id, data);
    }

    /**
     * 所有失败任务5s后重试(队列任务5s后重试)
     *
     * @param jobLogReq
     * @param data
     */
    public void sendAndRetry(MsgEnum jobLogReq, Object data) {
        long id = uniqueIdGeneratorService.nextId();
        RetryMessage<Object> message = new RetryMessage(id, data, 5 * 1000l);
        retryMessageQueue.offer(message);
    }

    public void receive(MessagePackProtobuf.Message data) {
        MessageContext message = new MessageContext(data.getMessageType(), data.getRequestId(), data.getMessage().toByteArray());
        //server对于初始化连接的元数据请求的回应，不属于业务数据，需要单独处理
        if (data.getMessageType().equals(MsgEnum.BROKER_METADATA_RESP.getCode())) {
            amethystClientHelper.receive(data);
        } else {
            //TODO
            //业务数据push进ringbuffer
            inputQueue.push(message);
        }
    }
}
