package de.evilcodez.classicprotocol;

import de.evilcodez.classicprotocol.packet.IPacket;
import de.evilcodez.classicprotocol.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private final TcpConnection connection;

    public PacketDecoder(TcpConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 1) {
            return;
        }
        byteBuf.markReaderIndex();
        final int packetId = byteBuf.readUnsignedByte();
        final IPacket packet = connection.getPacketRegistry().createPacket(packetId);
        if(packet == null) {
            throw new DecoderException("Bad packet id: " + packetId);
        }
        if(byteBuf.readableBytes() < packet.getSize()) {
            byteBuf.resetReaderIndex();
            return;
        }
        packet.deserialize(new PacketInputStream(connection, new ByteBufInputStream(byteBuf)));
        list.add(packet);
    }
}
