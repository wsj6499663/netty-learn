package netty.support;

import io.netty.channel.ChannelFuture;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import netty.clientnet.NettyClient;
import netty.clientnet.NettyClientImpl;
import netty.config.ConfigManager;
import netty.config.JobConfigBuilder;
import netty.consts.MsgEnum;
import netty.net.ClientNetworkService;
import netty.request.BrokerMetadataRequest;
import netty.request.JoinGroupRequest;
import netty.response.BrokerMetadataResponse;
import netty.util.ClientUtils;
import netty.util.NameUtil;
import netty.util.ProtobufUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private volatile ChannelFuture channelFuture;//just a  connect flag
    private final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
    //resident request
    private ConcurrentHashMap<Long, SynFuture<BrokerMetadataResponse>> concurrentHashMap = new ConcurrentHashMap<>();
    @Autowired
    private UniqueIdGeneratorService uniqueIdGeneratorService;
    private volatile NettyClient nettyClient;
    @Autowired
    private ClientNetworkService clientNetworkService;

    AmethystClientHelper() {

    }

    @PostConstruct
    public void init() {
        if (nettyClient == null) {
            synchronized (AmethystClientHelper.class) {
                if (nettyClient == null) {
                    nettyClient = new NettyClientImpl(clientNetworkService);
                }
            }
        }
        connect(MsgEnum.JOIN_GROUP_REQ, JobConfigBuilder.build());
    }

    private void connect(MsgEnum joinGroupReq, JoinGroupRequest build) {
        isConnected = false;
        if (channelFuture != null) {
            channelFuture.channel().close().syncUninterruptibly();
            channelFuture = null;
        }
        //connect start  asyn holder connect
        this.hashedWheelTimer.newTimeout(new ConnectTimerTask(joinGroupReq, build), 1l, TimeUnit.MICROSECONDS);

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
            while (channelFuture == null && !isClose) {
                if (delayTime < 10) {
                    delayTime += 2;
                } else {
                    delayTime = 10;
                }
                tryConnect(joinGroupReq, joinGroupRequest);
            }
        }
    }

    //channel connect
    private void tryConnect(MsgEnum joinGroupReq, JoinGroupRequest joinGroupRequest) {
        //parsing the server connecting address
        List<InetSocketAddress> urls = ClientUtils.parseAndValidateAddresses(ConfigManager.getString("amethyst.bootstrap.servers", "0.0.0.0"));
        while(urls.size()>0 && !isClose){
            this.currentAddress = (InetSocketAddress)urls.get(this.random.nextInt(10000) % urls.size());
            ChannelFuture channelFuture=nettyClient.connect(this.currentAddress);
            connectToGroupLeader(channelFuture,joinGroupReq,joinGroupRequest);
        }
    }

    private void connectToGroupLeader(ChannelFuture channelFuture, MsgEnum joinGroupReq, JoinGroupRequest joinGroupRequest) {
        BrokerMetadataResponse response;
        for(response=brokerMetadataRequest(channelFuture);;)

    }

    private BrokerMetadataResponse brokerMetadataRequest(ChannelFuture channelFuture) {
        BrokerMetadataRequest request = new BrokerMetadataRequest();
        request.setSarName(NameUtil.getName());
        SynFuture<BrokerMetadataResponse> synFuture=new SynFuture<BrokerMetadataResponse>();
        long requestId=uniqueIdGeneratorService.nextId();
        concurrentHashMap.put(requestId,synFuture);
        trySend(channelFuture,MsgEnum.BROKER_METADATA_REQ.getCode(),requestId,request);

    }

    private <T> void trySend(ChannelFuture channelFuture, String messageType, long requestId, T request) {
        channelFuture.channel().writeAndFlush(buildSendMessage(messageType,requestId, ProtobufUtil.serializer(request)));
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
    }

    private class cleanTask implements Runnable {

        @Override
        public void run() {

        }
    }
}
