package netty.support;

import com.google.common.base.Predicates;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelFuture;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import netty.clientnet.NettyClient;
import netty.clientnet.NettyClientImpl;
import netty.code.ResultCode;
import netty.config.ConfigManager;
import netty.config.JobConfigBuilder;
import netty.consts.MsgEnum;
import netty.net.ClientNetworkService;
import netty.protobuf.MessagePackProtobuf;
import netty.request.BrokerMetadataRequest;
import netty.request.JoinGroupRequest;
import netty.response.BrokerMetadataResponse;
import netty.util.ClientUtils;
import netty.util.NameUtil;
import netty.util.ProtobufUtil;
import netty.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AmethystClientHelper implements Closeable {
    private final Random random = new Random();
    private InetSocketAddress currentAddress;
    private volatile boolean isConnected = false;
    private volatile boolean isClose = false;//整个netty client 是否销毁
    private volatile ChannelFuture currentFuture;//just a  connect flag
    private final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
    //resident request
    private ConcurrentHashMap<Long, SynFuture<BrokerMetadataResponse>> concurrentHashMap = new ConcurrentHashMap<>();
    @Autowired
    private UniqueIdGeneratorService uniqueIdGeneratorService;
    private volatile NettyClient nettyClient;


    AmethystClientHelper() {

    }

    /**
     * 初始化连接，发送原始连接数据，确认远程服务连接可用性，以及接受远程连接建议,此阶段不发生业务数据交换，属于业务数据交换前的连接准备阶段
     * 后续netty pipeline中的handler需要对这种请求响应做特殊处理(和业务数据进行区分)
     *
     * @param clientNetworkService
     */
    public void init(ClientNetworkService clientNetworkService) {
        if (nettyClient == null) {
            synchronized (AmethystClientHelper.class) {
                if (nettyClient == null) {
                    //initiall pipline
                    nettyClient = new NettyClientImpl(clientNetworkService);
                }
            }
        }
        //向服务端发送相关客户定义的调度配置参数
        connect(MsgEnum.JOIN_GROUP_REQ, JobConfigBuilder.build());
    }

    private void connect(MsgEnum joinGroupReq, JoinGroupRequest build) {
        this.hashedWheelTimer.newTimeout(new ConnectTimerTask(joinGroupReq, build), 1l, TimeUnit.MICROSECONDS);

    }

    public void receive(MessagePackProtobuf.Message data) {
        brokerMetadataResponse(data.getRequestId(), ProtobufUtil.deserialize(data.getMessage().toByteArray(), BrokerMetadataResponse.class));

    }

    private void brokerMetadataResponse(long requestId, BrokerMetadataResponse response) {
        if (requestId == 0) {
            requestId = 1;
        }
        //TODO
        SynFuture<BrokerMetadataResponse> future = concurrentHashMap.get(requestId);
        if (null == future) {
            log.warn("Not found brokerMetadataRequest, maybe discarded when request is timeout. " + response);
        } else {
            future.setResponse(response);
        }
    }

    private class ConnectTimerTask implements TimerTask {

        MsgEnum joinGroupReq;
        JoinGroupRequest joinGroupRequest;

        ConnectTimerTask(MsgEnum joinGroupReq, JoinGroupRequest build) {
            this.joinGroupReq = joinGroupReq;
            this.joinGroupRequest = build;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            int delayTime = 0;
            //retry
            while (!isConnected && !isClose) {
                if (delayTime < 10) {
                    delayTime += 2;
                } else {
                    delayTime = 10;
                }
                tryConnect(joinGroupReq, joinGroupRequest);
                if (!isConnected && !isClose) {
                    ThreadUtil.sleep(delayTime * 1000);
                }
            }
        }
    }

    //channel connect
    private void tryConnect(MsgEnum joinGroupReq, JoinGroupRequest joinGroupRequest) {
        ChannelFuture channelFuture = null;
        //parsing the server connecting address
        //多地址用","分隔(随机选择一)
        //TODO
        List<InetSocketAddress> urls = ClientUtils.parseAndValidateAddresses(ConfigManager.getString("amethyst.bootstrap.servers", "0.0.0.0"));
        while (urls.size() > 0 && !isClose) {
            //此处可以加上权重或者负载算法
            this.currentAddress = urls.get(this.random.nextInt(10000) % urls.size());
            //netty lient to connect the server
            channelFuture = nettyClient.connect(this.currentAddress);
            try {
                //TODO
                if (channelFuture.sync().channel().isOpen()) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        connectToGroupLeader(channelFuture, joinGroupReq, joinGroupRequest);
    }

    private void connectToGroupLeader(ChannelFuture channelFuture, MsgEnum joinGroupReq, JoinGroupRequest joinGroupRequest) {
        Predicates.notNull().apply(channelFuture);
        InetSocketAddress inetSocketAddress = null;
        BrokerMetadataResponse response = brokerMetadataRequest(channelFuture);
        //the remote server tell the client to choice an other connect
        while (response != null && ResultCode.REDIRECT.getCode().equals(response.getResultCode()) && !isClose) {
            if (log.isDebugEnabled()) {
                log.debug("Redirect to server: [{}]", response.getBroker());
            }
            //TODO
            inetSocketAddress = new InetSocketAddress(response.getBroker(), 6000);
            channelFuture = redirectConnect(inetSocketAddress, channelFuture);
            //may be null
            response = brokerMetadataRequest(channelFuture);
        }
        if (response != null && response.getResultCode() == ResultCode.SUCCESS.getCode()) {
            if (channelFuture.isSuccess()) {
                // If connect to group leader success
                nettyClient.setChannel(channelFuture.channel());
                this.currentAddress = inetSocketAddress;
                setConnectedFlag(true);
                this.currentFuture = channelFuture;
                // Send joinGroupRequest
                joinGroupRequest(joinGroupReq, joinGroupRequest);
            }
        }


    }

    private void joinGroupRequest(MsgEnum type, JoinGroupRequest request) {
        // TODO Retry joinGroupRequest when server error!!!
        if (this.currentFuture != null && !isClose) {
            send(type, request);
            log.info("Send joinGroupRequest:{}", request);
        }
    }

    /**
     * Send message to server.
     *
     * @param type    type
     * @param message message
     * @param <T>     message instance
     */
    public <T> void send(MsgEnum type, T message) {
        //TODO
        send(type, -1L, message);
    }

    private ChannelFuture redirectConnect(InetSocketAddress currentAddress, ChannelFuture channelFuture) {
        //TODO
        if (channelFuture.channel().isOpen()) {
            channelFuture.channel().close();
            channelFuture.channel().deregister();
            //不在监听中结束
            isConnected = false;
        }
        return nettyClient.connect(currentAddress);
    }

    private BrokerMetadataResponse brokerMetadataRequest(ChannelFuture channelFuture) {
        BrokerMetadataResponse response = null;
        BrokerMetadataRequest request = new BrokerMetadataRequest();
        request.setSarName(NameUtil.getName());
        SynFuture<BrokerMetadataResponse> synFuture = new SynFuture<>();
        long requestId = uniqueIdGeneratorService.nextId();
        concurrentHashMap.put(requestId, synFuture);
        trySend(channelFuture, MsgEnum.BROKER_METADATA_REQ.getCode(), requestId, request);
        try {
            //may be we can get the result right now
            response = synFuture.get(15 * 1000, TimeUnit.MILLISECONDS);
            if (response != null) {
                log.info("get the response--{}--requestid---{}", response, requestId);
            } else {
                log.error("get the reponse from the remote is null--{}--{}", response, requestId);
            }
        } catch (Exception e) {
            log.error("execption is--{}", e);
        }
        return response;
    }

    private <T> void trySend(ChannelFuture channelFuture, String messageType, long requestId, T request) {
        if (!channelFuture.channel().isOpen()) {
            throw new IllegalStateException("the channel is not open");
        }
        ChannelFuture channelFuture1 = channelFuture.channel().writeAndFlush(buildSendMessage(messageType, requestId, ProtobufUtil.serializer(request)));
        channelFuture1.addListener(future -> {
            if (future.isDone()) {
                if (future.cause() != null) {
                    future.cause().printStackTrace();
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("send the message to the remote server is ok--{}--{}", request, requestId);
                    }
                }

            }
        });
    }

    //protobuf send data struct
    //TODO
    private MessagePackProtobuf.Message buildSendMessage(String messageType, long requestId, byte[] message) {
        MessagePackProtobuf.Message.Builder builder = MessagePackProtobuf.Message.newBuilder();
        builder.setMessageType(messageType);
        builder.setMessage(ByteString.copyFrom(message));
        if (requestId == -1L) {
            builder.setRequestId(uniqueIdGeneratorService.nextId());
        } else {
            builder.setRequestId(requestId);
        }
        return builder.build();
    }

    //销毁整个netty
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
        nettyClient.send(buildSendMessage(code, id, ProtobufUtil.serializer(data)));
    }

    private class cleanTask implements Runnable {

        @Override
        public void run() {

        }
    }

    private void setConnectedFlag(boolean isConnected) {
        this.isConnected = isConnected;
    }

}
