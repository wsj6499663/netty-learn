package netty.clientnet;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.message.MessagePacketProtobuf;
@ChannelHandler.Sharable
public class AmethystClientHandler extends SimpleChannelInboundHandler<MessagePacketProtobuf.Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePacketProtobuf.Message msg) throws Exception {

    }
}
