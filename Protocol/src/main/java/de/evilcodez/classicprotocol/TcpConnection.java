package de.evilcodez.classicprotocol;

import de.evilcodez.classicprotocol.packet.IPacket;
import de.evilcodez.classicprotocol.packet.PacketRegistry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;

public class TcpConnection extends SimpleChannelInboundHandler<IPacket> {

    private final PacketRegistry packetRegistry;
    private Channel channel;
    private InetSocketAddress remoteAddress;
    private IPacketHandler packetHandler;
    private String disconnectReason;
    private Throwable disconnectError;
    private boolean connected;

    public TcpConnection(PacketRegistry packetRegistry, IPacketHandler packetHandler) {
        this(packetRegistry, null, packetHandler);
    }

    public TcpConnection(PacketRegistry packetRegistry, Channel channel, IPacketHandler packetHandler) {
        this.packetRegistry = packetRegistry;
        this.channel = channel;
        this.packetHandler = packetHandler;
    }

    public void sendPacket(IPacket packet) {
        channel.writeAndFlush(packet);
    }

    public void sendPacket(IPacket packet, ChannelFutureListener listener) {
        channel.writeAndFlush(packet).addListener(listener);
    }

    public void sendPacketNoFlush(IPacket packet) {
        channel.write(packet);
    }

    public void sendPacketNoFlush(IPacket packet, ChannelFutureListener listener) {
        channel.write(packet).addListener(listener);
    }

    public void flush() {
        channel.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        this.remoteAddress = (InetSocketAddress) this.channel.remoteAddress();
        this.connected = true;
        this.packetHandler.onConnected();
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IPacket packet) throws Exception {
        packet.handle(packetHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(connected) {
            this.setDisconnectError(cause);
            this.disconnect();
        }
//        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(connected) {
            connected = false;
            packetHandler.onDisconnect(this.disconnectReason);
        }
        super.channelInactive(ctx);
    }

    public boolean isConnected() {
        return connected && channel != null && channel.isActive();
    }

    public void disconnect() {
        if(connected) {
            connected = false;
            packetHandler.onDisconnect(this.disconnectReason);
        }
        if(channel != null) channel.close();
    }

    public IPacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void setPacketHandler(IPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public String getDisconnectReason() {
        return disconnectReason;
    }

    public Throwable getDisconnectError() {
        return disconnectError;
    }

    public void setDisconnectError(Throwable disconnectError) {
        this.disconnectError = disconnectError;
    }

    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    public Channel getChannel() {
        return channel;
    }
}
