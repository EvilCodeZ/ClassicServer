package de.evilcodez.classicprotocol.packet;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;

public interface IPacket {

    void serialize(PacketOutputStream out) throws Exception;

    void deserialize(PacketInputStream in) throws Exception;

    void handle(IPacketHandler handler);

    int getSize();
}
