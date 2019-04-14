package netty.clientnet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import netty.net.ClientNetworkService;
import netty.protobuf.MessagePackProtobuf;

import java.util.concurrent.TimeUnit;


@Slf4j
public class AmethystClientInitializer extends ChannelInitializer<SocketChannel> {
    private final AmethystClientHandler clientHandler;

    private final IdleStateTriggerHandler idleStateTriggerHandler;

    public AmethystClientInitializer(ClientNetworkService clientNetworkService) {
        this.clientHandler = new AmethystClientHandler(clientNetworkService);
        this.idleStateTriggerHandler = new IdleStateTriggerHandler();
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new ProtobufVarint32FrameDecoder());
        p.addLast(new ProtobufDecoder(MessagePackProtobuf.Message.getDefaultInstance()));
        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        p.addLast(new ProtobufEncoder());
        p.addLast(new IdleStateHandler(30, 13, 0, TimeUnit.SECONDS));
        p.addLast(idleStateTriggerHandler);
        p.addLast(clientHandler);


    }
}
