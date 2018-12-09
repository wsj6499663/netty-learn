package netty.clientnet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import netty.net.ClientNetworkService;

public class AmethystClientInitializer extends ChannelInitializer<SocketChannel> {
    private ClientNetworkService clientNetworkService;
    public AmethystClientInitializer(ClientNetworkService clientNetworkService){
        this.clientNetworkService=clientNetworkService;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

    }
}
