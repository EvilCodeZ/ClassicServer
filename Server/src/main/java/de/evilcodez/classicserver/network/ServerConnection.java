package de.evilcodez.classicserver.network;

import de.evilcodez.classicprotocol.PacketDecoder;
import de.evilcodez.classicprotocol.PacketEncoder;
import de.evilcodez.classicprotocol.TcpConnection;
import de.evilcodez.classicprotocol.packet.PacketRegistry;
import de.evilcodez.classicserver.MinecraftServer;
import de.evilcodez.classicserver.player.AbstractPlayer;
import de.evilcodez.classicserver.player.PlayerEntity;
import de.evilcodez.classicserver.player.ServerNetworkHandler;
import de.evilcodez.classicserver.world.World;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerConnection {

    private final EventLoopGroup eventLoop;
    private final Channel serverChannel;

    public ServerConnection(String serverIp, int port, int threads) throws IOException {
        final boolean epoll = Epoll.isAvailable();
        this.eventLoop = epoll ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);

        this.serverChannel = new ServerBootstrap()
                .channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(eventLoop)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        final ServerNetworkHandler handler = new ServerNetworkHandler();
                        handler.setConnection(new TcpConnection(new PacketRegistry(), handler));
                        channel.pipeline()
                                .addLast(new ReadTimeoutHandler(30))
                                .addLast("decoder", new PacketDecoder(handler.getConnection()))
                                .addLast("encoder", new PacketEncoder(handler.getConnection()))
                                .addLast("packet_handler", handler.getConnection());
                    }
                }).bind(new InetSocketAddress(serverIp, port)).syncUninterruptibly().channel();
    }

    public void close() {
        for (World world : MinecraftServer.getInstance().getWorlds()) {
            for (AbstractPlayer player : world.getPlayers()) {
                if(player instanceof PlayerEntity) {
                    ((PlayerEntity) player).getNetworkHandler().disconnect("Server closed!");
                }
            }
        }
        eventLoop.shutdownGracefully();
    }

    public Channel getServerChannel() {
        return serverChannel;
    }
}
