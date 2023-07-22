package de.evilcodez.classicprotocol.packet.impl;

import de.evilcodez.classicprotocol.IPacketHandler;
import de.evilcodez.classicprotocol.PacketInputStream;
import de.evilcodez.classicprotocol.PacketOutputStream;
import de.evilcodez.classicprotocol.packet.IPacket;

public class Packet13Message implements IPacket {

    private int entityId;
    private String message;

    public Packet13Message() {}

    public Packet13Message(int entityId, String message) {
        this.entityId = entityId;
        this.message = message;
    }

    @Override
    public void serialize(PacketOutputStream out) throws Exception {
        out.writeByte(entityId);
        out.writeString(message);
    }

    @Override
    public void deserialize(PacketInputStream in) throws Exception {
        entityId = in.readUnsignedByte();
        message = in.readString();
    }

    @Override
    public int getSize() {
        return 1 + 64;
    }

    @Override
    public void handle(IPacketHandler handler) {
        handler.handle(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
