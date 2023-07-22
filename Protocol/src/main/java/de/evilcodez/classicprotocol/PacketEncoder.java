package de.evilcodez.classicprotocol;

import de.evilcodez.classicprotocol.packet.IPacket;
import de.evilcodez.classicprotocol.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacket> {

    private final TcpConnection connection;

    public PacketEncoder(TcpConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket packet, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(connection.getPacketRegistry().getPacketId(packet));
        packet.serialize(new PacketOutputStream(connection, new ByteBufOutputStream(byteBuf)));
    }
}
