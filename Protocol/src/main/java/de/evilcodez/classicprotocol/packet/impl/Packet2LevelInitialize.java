package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet2LevelInitialize implements IPacket {

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
