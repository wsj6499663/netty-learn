package netty.clientnet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.io.Closeable;
import java.net.InetSocketAddress;

public interface NettyClient extends Closeable {
    void init();

    ChannelFuture connect(InetSocketAddress inetSocketAddress);

    <T> void send(T t);

    void setChannel(Channel channel);
}
