package netty.clientnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import netty.error.AmethystException;
import netty.net.ClientNetworkService;
import org.springframework.core.NestedExceptionUtils;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClientImpl implements NettyClient {
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private ClientNetworkService clientNetworkService;
    private volatile Channel channel;

    public NettyClientImpl(ClientNetworkService clientNetworkService) {
        this.clientNetworkService = clientNetworkService;
        init();
    }

    @Override
    public void init() {
        this.eventLoopGroup = new NioEventLoopGroup(2);
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new AmethystClientInitializer(clientNetworkService));
    }

    @Override
    public ChannelFuture connect(InetSocketAddress inetSocketAddress) {
        ChannelFuture channelFuture = this.bootstrap.connect(inetSocketAddress).syncUninterruptibly();
        return channelFuture;
    }

    @Override
    public <T> void send(T t) {
        if (null == channel || !channel.isActive()) {
            throw new AmethystException("Not connected to the amethyst server yet.");
        }
        ChannelFuture channelFuture = channel.writeAndFlush(t);
        channelFuture.addListener(future -> {
            if (future.cause() != null) {
                log.error("发送失败--{}", NestedExceptionUtils.getRootCause(future.cause()).getLocalizedMessage());
            }
        });
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close().syncUninterruptibly();
            channel = null;
        }
        if (eventLoopGroup != null) {
            Future<?> future = eventLoopGroup.shutdownGracefully();
            if (future.isDone()) {
                eventLoopGroup = null;
            }

        }
        bootstrap = null;

    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
