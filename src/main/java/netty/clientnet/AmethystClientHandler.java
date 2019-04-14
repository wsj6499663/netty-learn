package netty.clientnet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netty.consts.MsgEnum;
import netty.net.ClientNetworkService;
import netty.protobuf.MessagePackProtobuf;

@AllArgsConstructor
@Slf4j
public class AmethystClientHandler extends SimpleChannelInboundHandler<MessagePackProtobuf.Message> {
    private ClientNetworkService clientNetworkService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePackProtobuf.Message msg) throws Exception {
        log.debug("Client channel read, type [{}], requestId [{}]", msg.getMessageType(), msg.getRequestId());
        if (msg.getMessageType().equals(MsgEnum.HEARTBEAT_RESP.getCode())) {
            log.debug("Received heartbeat response.");
        } else {
            clientNetworkService.receive(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("Client channel active, local ip [{}], remote ip [{}]", channel.localAddress().toString(), channel.remoteAddress().toString());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("Connection inactive, will be retry to the server.");
        super.channelInactive(ctx);
        //reconnect to the server
        clientNetworkService.init();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client channel caught exception, remote channel closed.", cause);
        ctx.close();
    }
}
