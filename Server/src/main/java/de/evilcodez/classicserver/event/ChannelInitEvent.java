package de.evilcodez.classicserver.event;

import de.evilcodez.classicserver.player.ServerNetworkHandler;
import io.netty.channel.Channel;

public class ChannelInitEvent {

    private final ServerNetworkHandler handler;
    private final Channel channel;

    public ChannelInitEvent(ServerNetworkHandler handler, Channel channel) {
        this.handler = handler;
        this.channel = channel;
    }

    public ServerNetworkHandler getHandler() {
        return handler;
    }

    public Channel getChannel() {
        return channel;
    }
}
