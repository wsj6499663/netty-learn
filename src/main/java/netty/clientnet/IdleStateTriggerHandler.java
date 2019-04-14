package netty.clientnet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import netty.consts.MsgEnum;
import netty.protobuf.MessagePackProtobuf;

@Slf4j
public class IdleStateTriggerHandler extends ChannelDuplexHandler {
    private static final MessagePackProtobuf.Message HEARTBEAT_PING
            = MessagePackProtobuf.Message.newBuilder().setMessageType(MsgEnum.HEARTBEAT_REQ.getCode()).build();

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                log.debug("HEARTBEAT_PING");
                ctx.writeAndFlush(HEARTBEAT_PING);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
