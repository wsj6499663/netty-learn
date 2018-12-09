package netty.clientnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.net.ClientNetworkService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NettyClientImpl implements NettyClient {
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private ClientNetworkService clientNetworkService;

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
        return this.bootstrap.connect(inetSocketAddress).syncUninterruptibly();
    }

    @Override
    public <T> void send(T t) {

    }

    @Override
    public void close() throws IOException {

    }
}
