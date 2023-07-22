package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet12DespawnPlayer implements IPacket {

    private int entityId;

    public Packet12DespawnPlayer() {}

    public Packet12DespawnPlayer(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(entityId);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        entityId = in.readUnsignedByte();
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public int getSize() {
        return 1;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
